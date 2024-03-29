package service.read;

import java.util.List;
import java.util.Map;

public interface FileReader {
    Map<String, String> readDoubleSlashesFromAllLinesFromFiles(List<String> allLinesFromFiles);

    Map<String, String> readDescriptionsFromAllLinesFromFiles(List<String> allLinesFromFiles);

    Map<String, String> readBLogEntriesFromSingleFile(List<String> fileContent);

    Map<String, String> readInfoAddChoicesFromSingleFile(List<String> fileContent);

    List<String> readEntireFile(String fileName);

    String getValueForLine(String line);

    List<String> getFileContent(String fileName);
}
