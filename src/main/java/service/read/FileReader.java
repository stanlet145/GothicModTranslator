package service.read;

import java.util.List;
import java.util.Map;

public interface FileReader {
    Map<String, String> readDoubleSlashesFromSingleFile(String fileName);
    List<String> readEntireFile(String fileName);
    String getValueForLine(String line);
}
