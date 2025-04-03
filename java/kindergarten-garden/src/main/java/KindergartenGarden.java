import java.util.List;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class KindergartenGarden {

    private static final List<String> STUDENTS = Arrays.asList(
            "Alice", "Bob", "Charlie", "David", "Eve", "Fred",
            "Ginny", "Harriet", "Ileana", "Joseph", "Kincaid", "Larry"
    );

    private final String row1;
    private final String row2;

    KindergartenGarden(String garden) {
        String[] rows = garden.split("\n");
        this.row1 = rows[0];
        this.row2 = rows[1];
    }

    List<Plant> getPlantsOfStudent(String student) {
        int index = STUDENTS.indexOf(student);
        int pos = index * 2;

        List<Plant> plants = new ArrayList<>(4);
        plants.add(Plant.getPlant(row1.charAt(pos)));
        plants.add(Plant.getPlant(row1.charAt(pos + 1)));
        plants.add(Plant.getPlant(row2.charAt(pos)));
        plants.add(Plant.getPlant(row2.charAt(pos + 1)));

        return plants;
    }

}
