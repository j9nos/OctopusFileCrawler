import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;

public class OctopusFileCrawler extends SimpleFileVisitor<Path> {
    private static final Path DIRECTORY = Paths.get(".");
    private final LinkedList<String> keywords;

    public OctopusFileCrawler(final String... keywords) {
        this.keywords = new LinkedList<>(List.of(keywords));
    }

    @Override
    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
        try {
            if (containsKeywords(file)) {
                System.out.println(file);
            }
        } catch (final Exception e) {
        }
        return FileVisitResult.CONTINUE;
    }

    public void crawl() throws IOException {
        Files.walkFileTree(DIRECTORY, this);
    }

    public boolean containsKeywords(final Path file) throws IOException {
        final LinkedList<String> copy = new LinkedList<>(keywords);
        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line;
            while ((line = br.readLine()) != null) {
                for (int i = copy.size() - 1; i >= 0; i--) {
                    if (line.toLowerCase().contains(copy.get(i).toLowerCase())) {
                        copy.remove(i);
                    }
                }
                if (copy.size() == 0) {
                    return true;
                }
            }
            return copy.size() == 0;
        }
    }

    public static void main(final String... args) throws IOException {
        new OctopusFileCrawler(args).crawl();
    }
}
