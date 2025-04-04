import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


class KindergartenGarden {

    private final Map<String, List<Plant>> studentPlants = new HashMap<>();
    private static final List<String> STUDENTS = Arrays.asList(
            "Alice", "Bob", "Charlie", "David", "Eve", "Fred",
            "Ginny", "Harriet", "Ileana", "Joseph", "Kincaid", "Larry"
    );

    KindergartenGarden(String garden) {
        String[] rows = garden.split("\n");
        if (rows.length != 2) {
            throw new IllegalArgumentException("Garden layout must have exactly two rows.");
        }
        String row1 = rows[0];
        String row2 = rows[1];

        // Validate row lengths are equal and even
        if (row1.length() != row2.length() || row1.length() % 2 != 0) {
             throw new IllegalArgumentException("Invalid garden dimensions: Rows must have equal, even length.");
        }

        // Determine the number of students based on the diagram size
        int numberOfStudentsInDiagram = row1.length() / 2;
        if (numberOfStudentsInDiagram > STUDENTS.size()) {
             throw new IllegalArgumentException("Garden diagram represents more students than the class size.");
        }

        // Process plants for the students present in the diagram
        for (int i = 0; i < numberOfStudentsInDiagram; i++) {
            String student = STUDENTS.get(i);
            int startIndex = i * 2;

            List<Plant> plants = new ArrayList<>();
            plants.add(Plant.getPlant(row1.charAt(startIndex)));
            plants.add(Plant.getPlant(row1.charAt(startIndex + 1)));
            plants.add(Plant.getPlant(row2.charAt(startIndex)));
            plants.add(Plant.getPlant(row2.charAt(startIndex + 1)));

            studentPlants.put(student, plants);
        }
    }

    List<Plant> getPlantsOfStudent(String student) {
        if (!studentPlants.containsKey(student)) {
             throw new IllegalArgumentException("Student not found: " + student);
        }
        return studentPlants.get(student);
    }

}
