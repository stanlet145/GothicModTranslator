package service.files;

import io.vavr.control.Try;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileProcessingServiceImpl implements FileProcessingService {

    private static final String READ_FILES_FROM_DICTIONARY = "files/from";
    private static final String READ_FILES_TO_DICTIONARY = "files/to";

    @Override
    public Map<String, String> mapProcessedFiles() {
       return createFilesFromFilesToMap(
                tryReadAllFilesGivenDirectory(READ_FILES_FROM_DICTIONARY),
                tryReadAllFilesGivenDirectory(READ_FILES_TO_DICTIONARY)
        );
    }

    private Map<String, String> createFilesFromFilesToMap(List<File> filesFrom, List<File> filesTo) {
        return buildMapFromLists(new HashMap<>(), filesFrom, filesTo);
    }

    private List<File> tryReadAllFilesGivenDirectory(String directoryPath) {
        return Try.of(() -> Files.walk(Paths.get(directoryPath))
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .collect(Collectors.toList()))
                .onFailure(System.out::println)
                .get();
    }

    private Optional<File> findEqualFileForGivenFileName(String searchedFileName, List<File> searchedFiles) {
        return searchedFiles.stream().filter(file -> file.getName().equals(searchedFileName)).findFirst();
    }

    private Map<String, String> buildMapFromLists(Map<String, String> map, List<File> filesFrom, List<File> filesTo) {
        filesFrom.forEach(file -> findEqualFileForGivenFileName(file.getName(), filesTo)
                .ifPresent(comparedFile -> map.put(file.getName(), comparedFile.getName())));
        return map;
    }
}
