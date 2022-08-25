package service.files;

import service.read.FileReader;
import service.read.FileReaderImpl;
import service.utils.FileReadingUtils;
import service.write.FileWriter;
import service.write.FileWriterImpl;

import java.io.File;
import java.util.*;

public class FileProcessingServiceImpl implements FileProcessingService {

    private final FileWriter fileWriter = new FileWriterImpl();
    private final FileReader fileReader = new FileReaderImpl();

    private static final String READ_FILES_FROM_DICTIONARY = "..\\files\\from";
    private static final String READ_FILES_TO_DICTIONARY = "..\\files\\to";

    @Override
    public void processFiles() {
        var allLinesFromFiles = new ArrayList<String>();
        prepareFilesToBeWrittenFrom().forEach(file ->
                allLinesFromFiles.addAll(fileReader.readEntireFile(file.getPath())));
        prepareFilesToBeWrittenTo().forEach(file -> fileWriter.writeBetweenFiles(allLinesFromFiles, file.getPath()));
    }

    private List<File> prepareFilesToBeWrittenTo() {
        return tryReadAllFilesGivenDirectory(READ_FILES_TO_DICTIONARY);
    }

    private List<File> prepareFilesToBeWrittenFrom() {
        return tryReadAllFilesGivenDirectory(READ_FILES_FROM_DICTIONARY);
    }

    private List<File> tryReadAllFilesGivenDirectory(String directoryPath) {
        return FileReadingUtils.tryReadAllFilesByPath(directoryPath);
    }

    private Optional<File> findEqualFileForGivenFileName(String searchedFileName, List<File> searchedFiles) {
        return searchedFiles.stream().filter(file -> file.getName().equals(searchedFileName)).findFirst();
    }
}
