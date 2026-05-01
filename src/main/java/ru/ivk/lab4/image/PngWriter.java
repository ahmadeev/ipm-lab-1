package ru.ivk.lab4.image;

import ru.ivk.lab4.core.ColorRgb;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.CRC32;
import java.util.zip.DeflaterOutputStream;

public final class PngWriter {
    private static final byte[] PNG_SIGNATURE = new byte[]{
            (byte) 137, 80, 78, 71, 13, 10, 26, 10
    };

    private PngWriter() {
    }

    public static void write(
            ImageBuffer image,
            Path outputPath,
            NormalizationMode normalizationMode,
            double fixedExposure,
            double gamma
    ) throws IOException {
        Path parent = outputPath.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        ByteArrayOutputStream pngBytes = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(pngBytes);

        output.write(PNG_SIGNATURE);
        writeChunk(output, "IHDR", buildIhdr(image));
        writeChunk(output, "IDAT", buildIdat(image, normalizationMode, fixedExposure, gamma));
        writeChunk(output, "IEND", new byte[0]);
        output.flush();

        Files.write(outputPath, pngBytes.toByteArray());
    }

    private static byte[] buildIhdr(ImageBuffer image) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(bytes);

        output.writeInt(image.getWidth());
        output.writeInt(image.getHeight());
        output.writeByte(8);
        output.writeByte(2);
        output.writeByte(0);
        output.writeByte(0);
        output.writeByte(0);
        output.flush();

        return bytes.toByteArray();
    }

    private static byte[] buildIdat(
            ImageBuffer image,
            NormalizationMode normalizationMode,
            double fixedExposure,
            double gamma
    ) throws IOException {
        double exposure = ImageColorMapper.resolveExposure(image, normalizationMode, fixedExposure);
        ByteArrayOutputStream compressedBytes = new ByteArrayOutputStream();

        try (DeflaterOutputStream deflater = new DeflaterOutputStream(compressedBytes)) {
            for (int y = image.getHeight() - 1; y >= 0; y--) {
                deflater.write(0);

                for (int x = 0; x < image.getWidth(); x++) {
                    ColorRgb color = image.getPixel(x, y);

                    deflater.write(ImageColorMapper.toByte(color, exposure, gamma, 0));
                    deflater.write(ImageColorMapper.toByte(color, exposure, gamma, 1));
                    deflater.write(ImageColorMapper.toByte(color, exposure, gamma, 2));
                }
            }
        }

        return compressedBytes.toByteArray();
    }

    private static void writeChunk(DataOutputStream output, String type, byte[] data) throws IOException {
        byte[] typeBytes = type.getBytes(StandardCharsets.US_ASCII);
        CRC32 crc = new CRC32();

        crc.update(typeBytes);
        crc.update(data);

        output.writeInt(data.length);
        output.write(typeBytes);
        output.write(data);
        output.writeInt((int) crc.getValue());
    }
}
