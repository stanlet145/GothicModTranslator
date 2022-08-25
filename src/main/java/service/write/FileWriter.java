package service.write;

import java.util.List;

public interface FileWriter {
    void writeBetweenFiles(List<String> allLinesFromFiles, String to);
}
