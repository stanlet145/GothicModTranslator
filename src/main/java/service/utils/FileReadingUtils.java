package service.utils;

import io.vavr.control.Try;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileReadingUtils {

    public static List<File> tryReadAllFilesByPath(String path) {
        return Try.of(() -> Files.walk(Paths.get(path))
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .collect(Collectors.toList()))
                .onFailure(System.out::println)
                .get();
    }
}
