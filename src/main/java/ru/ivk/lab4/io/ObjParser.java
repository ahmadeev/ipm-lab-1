package ru.ivk.lab4.io;

import ru.ivk.common.math.Vec3;
import ru.ivk.lab4.geometry.Triangle;
import ru.ivk.lab4.material.Material;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ObjParser {
    public List<Triangle> parse(Path path, Material material) throws IOException {
        List<Vec3> vertices = new ArrayList<>();
        List<Triangle> triangles = new ArrayList<>();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        for (String rawLine : lines) {
            String line = removeComment(rawLine).trim();

            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split("\\s+");

            if ("v".equals(parts[0])) {
                vertices.add(parseVertex(parts));
            } else if ("f".equals(parts[0])) {
                addFace(parts, vertices, triangles, material);
            }
        }

        return triangles;
    }

    private static Vec3 parseVertex(String[] parts) {
        if (parts.length < 4) {
            throw new IllegalArgumentException("OBJ vertex must contain x y z");
        }

        return new Vec3(
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2]),
                Double.parseDouble(parts[3])
        );
    }

    private static void addFace(String[] parts, List<Vec3> vertices, List<Triangle> triangles, Material material) {
        if (parts.length < 4) {
            throw new IllegalArgumentException("OBJ face must contain at least 3 vertices");
        }

        int[] indices = new int[parts.length - 1];

        for (int i = 1; i < parts.length; i++) {
            indices[i - 1] = parseVertexIndex(parts[i], vertices.size());
        }

        for (int i = 1; i < indices.length - 1; i++) {
            triangles.add(new Triangle(
                    vertices.get(indices[0]),
                    vertices.get(indices[i]),
                    vertices.get(indices[i + 1]),
                    material
            ));
        }
    }

    private static int parseVertexIndex(String token, int vertexCount) {
        String[] parts = token.split("/");
        int rawIndex = Integer.parseInt(parts[0]);

        if (rawIndex > 0) {
            int index = rawIndex - 1;

            if (index >= 0 && index < vertexCount) {
                return index;
            }

            throw new IllegalArgumentException("OBJ vertex index is outside loaded vertices");
        }

        if (rawIndex < 0) {
            int index = vertexCount + rawIndex;

            if (index >= 0 && index < vertexCount) {
                return index;
            }

            throw new IllegalArgumentException("OBJ vertex index is outside loaded vertices");
        }

        throw new IllegalArgumentException("OBJ indices are 1-based and must not be 0");
    }

    private static String removeComment(String line) {
        int commentIndex = line.indexOf('#');
        return commentIndex >= 0 ? line.substring(0, commentIndex) : line;
    }
}
