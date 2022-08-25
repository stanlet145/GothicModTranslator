package service.read;

import java.util.List;
import java.util.Map;

public interface FileReader {
    Map<String, String> readDoubleSlashesFromAllLinesFromFiles(List<String> fileContent);

    List<String> readDescriptionsFromSingleFile(List<String> fileContent);

    List<String> readBLogEntriesFromSingleFile(List<String> fileContent);

    List<String> readInfoAddChoicesFromSingleFile(List<String> fileContent);

    List<String> readEntireFile(String fileName);

    String getValueForLine(String line);

    List<String> getFileContent(String fileName);
}
