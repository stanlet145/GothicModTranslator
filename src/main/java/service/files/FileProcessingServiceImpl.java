package service.files;

import service.utils.FileReadingUtils;
import service.write.FileWriter;
import service.write.FileWriterImpl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FileProcessingServiceImpl implements FileProcessingService {

    private final FileWriter fileWriter = new FileWriterImpl();

    private static final String READ_FILES_FROM_DICTIONARY = "..\\files\\from";
    private static final String READ_FILES_TO_DICTIONARY = "..\\files\\to";

    @Override
    public void processFiles() {
        prepareFilesToProcess().forEach(fileWriter::writeBetweenFiles);
    }

    private Map<String, String> prepareFilesToProcess() {
        return createFilesFromFilesToMap(
                tryReadAllFilesGivenDirectory(READ_FILES_FROM_DICTIONARY),
                tryReadAllFilesGivenDirectory(READ_FILES_TO_DICTIONARY)
        );
    }

    private Map<String, String> createFilesFromFilesToMap(List<File> filesFrom, List<File> filesTo) {
        return buildMapFromLists(new HashMap<>(), filesFrom, filesTo);
    }

    private List<File> tryReadAllFilesGivenDirectory(String directoryPath) {
        return FileReadingUtils.tryReadAllFilesByPath(directoryPath);
    }

    private Optional<File> findEqualFileForGivenFileName(String searchedFileName, List<File> searchedFiles) {
        return searchedFiles.stream().filter(file -> file.getName().equals(searchedFileName)).findFirst();
    }

    private Map<String, String> buildMapFromLists(Map<String, String> map, List<File> filesFrom, List<File> filesTo) {
        filesFrom.forEach(file -> findEqualFileForGivenFileName(file.getName(), filesTo)
                .ifPresent(comparedFile -> map.put(file.getPath(), comparedFile.getPath())));
        return map;
    }
}
