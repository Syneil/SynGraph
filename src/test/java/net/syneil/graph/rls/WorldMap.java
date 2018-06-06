package net.syneil.graph.rls;

import net.syneil.graph.Edge;
import net.syneil.graph.Graph;
import net.syneil.graph.GraphProperties;
import net.syneil.graph.MutableGraph;
import net.syneil.graph.edge.UnlabelledEdge;
import net.syneil.graph.edgelist.EdgeListGraph;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Predicate.isEqual;
import static net.syneil.graph.GraphProperties.Orientation.UNDIRECTED;
import static org.junit.jupiter.api.Assertions.*;

public class WorldMap {
    private static final String[] NATIONS = {
            "Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Antigua and Barbuda", "Argentina", "Armenia",
            "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium",
            "Belize", "Benin", "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Botswana", "Brazil", "Brunei",
            "Bulgaria", "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde",
            "Central African Republic", "Chad", "Chile", "China", "Colombia", "Comoros",
            "Democratic Republic of the Congo", "Republic of the Congo", "Costa Rica", "Côte d'Ivoire", "Croatia",
            "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "East Timor",
            "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia", "Fiji", "Finland",
            "France", "Gabon", "The Gambia", "Georgia", "Germany", "Ghana", "Greece", "Grenada", "Guatemala", "Guinea",
            "Guinea-Bissau", "Guyana", "Haiti", "Honduras", "Hungary", "Iceland", "India", "Indonesia", "Iran", "Iraq",
            "Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati",
            "North Korea", "South Korea", "Kuwait", "Kyrgyzstan", "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia",
            "Libya", "Liechtenstein", "Lithuania", "Luxembourg", "Macedonia", "Madagascar", "Malawi", "Malaysia",
            "Maldives", "Mali", "Malta", "Marshall Islands", "Mauritania", "Mauritius", "Mexico", "Micronesia",
            "Moldova", "Monaco", "Mongolia", "Montenegro", "Morocco", "Mozambique", "Myanmar", "Namibia", "Nauru",
            "Nepal", "Netherlands", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Norway", "Oman", "Pakistan",
            "Palau", "Palestine", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Poland", "Portugal",
            "Qatar", "Romania", "Russia", "Rwanda", "Saint Kitts and Nevis", "Saint Lucia",
            "Saint Vincent and the Grenadines", "Samoa", "San Marino", "São Tomé and Príncipe", "Saudi Arabia",
            "Senegal", "Serbia", "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "Solomon Islands",
            "Somalia", "South Africa", "South Sudan", "Spain", "Sri Lanka", "Sudan", "Suriname", "Swaziland", "Sweden",
            "Switzerland", "Syria", "Tajikistan", "Tanzania", "Thailand", "Togo", "Tonga", "Trinidad and Tobago",
            "Tunisia", "Turkey", "Turkmenistan", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates",
            "United Kingdom", "United States", "Uruguay", "Uzbekistan", "Vanuatu", "Vatican City", "Venezuela",
            "Vietnam", "Yemen", "Zambia", "Zimbabwe"
    };
    private static final Graph<String, Edge<String>> WORLD;

    private static Function<String, Edge<String>> toEdgeFrom(String source) {
        return target -> new UnlabelledEdge<>(source, target);
    }

    private static void addBorders(MutableGraph<String, Edge<String>> graph,
                                   String country,
                                   String... borders) {
        Stream.of(borders)
              .map(toEdgeFrom(country))
              .forEach(graph::addEdge);
    }

    static {
        var graph =
                new EdgeListGraph<String, Edge<String>>(GraphProperties.builder().orientation(UNDIRECTED).build());
        Stream.of(NATIONS).forEach(graph::addVertex);
        addBorders(graph, "Afghanistan", "China", "Iran", "Pakistan", "Tajikistan", "Uzbekistan");
        addBorders(graph, "Albania", "Greece", "Macedonia", "Montenegro");
        addBorders(graph, "Algeria", "Libya", "Mali", "Mauritania", "Morocco", "Niger", "Tunisia");
        addBorders(graph, "Andorra", "France", "Spain");
        addBorders(graph, "Angola", "Democratic Republic of the Congo", "Republic of the Congo", "Namibia", "Zambia");
        addBorders(graph, "Chile", "Argentina");
        addBorders(graph, "Argentina", "Bolivia", "Brazil", "Chile", "Paraguay", "Uruguay");
        addBorders(graph, "Armenia", "Azerbaijan", "Georgia", "Iran", "Turkey");
        addBorders(graph, "Austria", "Czech Republic", "Germany", "Hungary", "Italy", "Liechtenstein", "Slovakia",
                   "Slovenia", "Switzerland");
        addBorders(graph, "Azerbaijan", "Armenia", "Georgia", "Iran", "Russia", "Turkey");
        addBorders(graph, "Bangladesh", "India", "Myanmar");
        addBorders(graph, "Belarus", "Latvia", "Lithuania", "Poland", "Russia", "Ukraine");
        addBorders(graph, "Belgium", "France", "Germany", "Luxembourg", "Netherlands");
        addBorders(graph, "Belize", "Guatemala", "Mexico");
        addBorders(graph, "Benin", "Burkina Faso", "Niger", "Nigeria", "Togo");
        addBorders(graph, "Bhutan", "China", "India");
        addBorders(graph, "Bolivia", "Argentina", "Brazil", "Chile", "Paraguay", "Peru");
        addBorders(graph, "Bosnia and Herzegovina", "Croatia", "Montenegro", "Serbia");
        addBorders(graph, "Botswana", "Namibia", "South Africa", "Zambia", "Zimbabwe");
        addBorders(graph, "Brazil", "Argentina", "Bolivia", "Colombia", "Guyana", "Paraguay", "Peru",
                   "Suriname", "Uruguay", "Venezuela");
        addBorders(graph, "Brunei", "Malaysia");
        addBorders(graph, "Bulgaria", "Greece", "Macedonia", "Romania", "Serbia", "Turkey");
        addBorders(graph, "Burkina Faso", "Benin", "Côte d'Ivoire", "Ghana", "Mali", "Niger", "Togo");
        addBorders(graph, "Burundi", "Democratic Republic of the Congo", "Rwanda", "Tanzania");
        addBorders(graph, "Cambodia", "Laos", "Thailand", "Vietnam");
        addBorders(graph, "Cameroon", "Central African Republic", "Chad", "Republic of the Congo", "Equatorial Guinea",
                   "Gabon", "Nigeria");
        addBorders(graph, "Canada", "United States");
        addBorders(graph, "Central African Republic", "Cameroon", "Chad", "Democratic Republic of the Congo",
                   "Republic of the Congo", "South Sudan", "Sudan");
        addBorders(graph, "Chad", "Cameroon", "Central African Republic", "Libya", "Niger", "Nigeria", "Sudan");
        addBorders(graph, "Chile", "Argentina", "Bolivia", "Peru");
        addBorders(graph, "China", "Afghanistan", "Bhutan", "India", "Kazakhstan", "North Korea", "Kyrgyzstan", "Laos",
                   "Mongolia", "Myanmar", "Nepal", "Pakistan", "Russia", "Tajikistan", "Vietnam");
        addBorders(graph, "Colombia", "Brazil", "Ecuador", "Panama", "Peru", "Venezuela");
        addBorders(graph, "Democratic Republic of the Congo", "Angola", "Burundi", "Central African Republic",
                   "Republic of the Congo", "Rwanda", "South Sudan", "Tanzania", "Uganda", "Zambia");
        addBorders(graph, "Republic of the Congo", "Angola", "Cameroon", "Central African Republic",
                   "Democratic Republic of the Congo", "Gabon");
        addBorders(graph, "Costa Rica", "Nicaragua", "Panama");
        addBorders(graph, "Côte d'Ivoire", "Burkina Faso", "Ghana", "Guinea", "Liberia", "Mali");
        addBorders(graph, "Croatia", "Bosnia and Herzegovina", "Hungary", "Montenegro", "Serbia", "Slovenia");
        addBorders(graph, "Czech Republic", "Austria", "Germany", "Poland", "Slovakia");
        addBorders(graph, "Denmark", "Germany");
        addBorders(graph, "Djibouti", "Eritrea", "Ethiopia", "Somalia");
        addBorders(graph, "Dominican Republic", "Haiti");
        addBorders(graph, "East Timor", "Indonesia");
        addBorders(graph, "Ecuador", "Colombia", "Peru");
        addBorders(graph, "Egypt", "Palestine", "Israel", "Libya", "Sudan");
        addBorders(graph, "El Salvador", "Guatemala", "Honduras");
        addBorders(graph, "Equatorial Guinea", "Cameroon", "Gabon");
        addBorders(graph, "Eritrea", "Djibouti", "Ethiopia", "Sudan");
        addBorders(graph, "Estonia", "Latvia", "Russia");
        addBorders(graph, "Ethiopia", "Djibouti", "Eritrea", "Kenya", "Somalia", "South Sudan", "Sudan");
        addBorders(graph, "Finland", "Norway", "Sweden", "Russia");
        addBorders(graph, "France", "Andorra", "Belgium", "Germany", "Italy", "Luxembourg", "Monaco",
                   "Netherlands", "Spain", /*"Suriname",*/ "Switzerland");
        addBorders(graph, "Gabon", "Cameroon", "Republic of the Congo", "Equatorial Guinea");
        addBorders(graph, "The Gambia", "Senegal");
        addBorders(graph, "Palestine", "Israel", "Egypt");
        addBorders(graph, "Georgia", "Armenia", "Azerbaijan", "Russia", "Turkey");
        addBorders(graph, "Germany", "Austria", "Belgium", "Czech Republic", "Denmark", "France", "Luxembourg",
                   "Netherlands", "Poland", "Switzerland");
        addBorders(graph, "Ghana", "Burkina Faso", "Côte d'Ivoire", "Togo");
        addBorders(graph, "Greece", "Albania", "Bulgaria", "Turkey", "Macedonia");
        addBorders(graph, "Guatemala", "Belize", "El Salvador", "Honduras", "Mexico");
        addBorders(graph, "Guinea", "Côte d'Ivoire", "Guinea-Bissau", "Liberia", "Mali", "Senegal", "Sierra Leone");
        addBorders(graph, "Guinea-Bissau", "Guinea", "Senegal");
        addBorders(graph, "Guyana", "Brazil", "Suriname", "Venezuela");
        addBorders(graph, "Haiti", "Dominican Republic");
        addBorders(graph, "Honduras", "Guatemala", "El Salvador", "Nicaragua");
        addBorders(graph, "Hungary", "Austria", "Croatia", "Romania", "Serbia", "Slovakia", "Slovenia", "Ukraine");
        addBorders(graph, "India", "Bangladesh", "Bhutan", "China", "Myanmar", "Nepal", "Pakistan");
        addBorders(graph, "Indonesia", "East Timor", "Malaysia", "Papua New Guinea");
        addBorders(graph, "Iran", "Afghanistan", "Armenia", "Azerbaijan", "Iraq", "Pakistan", "Turkey", "Turkmenistan");
        addBorders(graph, "Iraq", "Iran", "Jordan", "Kuwait", "Saudi Arabia", "Syria", "Turkey");
        addBorders(graph, "Ireland", "United Kingdom");
        addBorders(graph, "Israel", "Egypt", "Palestine", "Jordan", "Lebanon", "Syria");
        addBorders(graph, "Italy", "Austria", "France", "San Marino", "Slovenia", "Switzerland", "Vatican City");
        addBorders(graph, "Jordan", "Iraq", "Israel", "Saudi Arabia", "Syria", "Palestine");
        addBorders(graph, "Kazakhstan", "China", "Kyrgyzstan", "Russia", "Turkmenistan", "Uzbekistan");
        addBorders(graph, "Kenya", "Ethiopia", "Somalia", "South Sudan", "Tanzania", "Uganda");
        addBorders(graph, "North Korea", "China", "South Korea", "Russia");
        addBorders(graph, "South Korea", "North Korea");
        addBorders(graph, "Kuwait", "Iraq", "Saudi Arabia");
        addBorders(graph, "Kyrgyzstan", "China", "Kazakhstan", "Tajikistan", "Uzbekistan");
        addBorders(graph, "Laos", "Cambodia", "China", "Myanmar", "Thailand", "Vietnam");
        addBorders(graph, "Latvia", "Belarus", "Estonia", "Lithuania", "Russia");
        addBorders(graph, "Israel", "Syria");
        addBorders(graph, "Lesotho", "South Africa");
        addBorders(graph, "Liberia", "Guinea", "Côte d'Ivoire", "Sierra Leone");
        addBorders(graph, "Libya", "Algeria", "Chad", "Egypt", "Niger", "Sudan", "Tunisia");
        addBorders(graph, "Liechtenstein", "Austria", "Switzerland");
        addBorders(graph, "Lithuania", "Belarus", "Latvia", "Poland", "Russia");
        addBorders(graph, "Luxembourg", "Belgium", "France", "Germany");
        addBorders(graph, "Macedonia", "Albania", "Bulgaria", "Greece", "Serbia");
        addBorders(graph, "Malawi", "Mozambique", "Tanzania", "Zambia");
        addBorders(graph, "Malaysia", "Brunei", "Indonesia", "Thailand");
        addBorders(graph, "Mali", "Algeria", "Burkina Faso", "Côte d'Ivoire", "Guinea", "Mauritania", "Niger",
                   "Senegal");
        addBorders(graph, "Mauritania", "Algeria", "Mali", "Senegal");
        addBorders(graph, "Mexico", "Belize", "Guatemala", "United States");
        addBorders(graph, "Moldova", "Romania", "Ukraine");
        addBorders(graph, "Monaco", "France");
        addBorders(graph, "Mongolia", "China", "Russia");
        addBorders(graph, "Montenegro", "Albania", "Bosnia and Herzegovina", "Croatia", "Serbia");
        addBorders(graph, "Morocco", "Algeria", "Spain");
        addBorders(graph, "Mozambique", "Malawi", "South Africa", "Swaziland", "Tanzania", "Zambia", "Zimbabwe");
        addBorders(graph, "Myanmar", "Bangladesh", "China", "India", "Laos", "Thailand");
        addBorders(graph, "Namibia", "Angola", "Botswana", "South Africa", "Zambia");
        addBorders(graph, "Nepal", "China", "India");
        addBorders(graph, "Netherlands", "Belgium", "Germany", "France");
        addBorders(graph, "Nicaragua", "Costa Rica", "Honduras");
        addBorders(graph, "Niger", "Algeria", "Benin", "Burkina Faso", "Chad", "Libya", "Mali", "Nigeria");
        addBorders(graph, "Nigeria", "Benin", "Cameroon", "Chad", "Niger");
        addBorders(graph, "Norway", "Finland", "Sweden", "Russia");
        addBorders(graph, "Oman", "Saudi Arabia", "United Arab Emirates");
        addBorders(graph, "Pakistan", "Afghanistan", "China", "India", "Iran");
        addBorders(graph, "Palestine", "Egypt", "Israel", "Jordan");
        addBorders(graph, "Panama", "Colombia", "Costa Rica");
        addBorders(graph, "Papua New Guinea", "Indonesia");
        addBorders(graph, "Paraguay", "Argentina", "Bolivia", "Brazil");
        addBorders(graph, "Peru", "Bolivia", "Brazil", "Chile", "Colombia", "Ecuador");
        addBorders(graph, "Poland", "Belarus", "Czech Republic", "Germany", "Lithuania", "Russia", "Slovakia",
                   "Ukraine");
        addBorders(graph, "Portugal", "Spain");
        addBorders(graph, "Qatar", "Saudi Arabia");
        addBorders(graph, "Norway", "Australia", "United Kingdom");
        addBorders(graph, "Romania", "Bulgaria", "Hungary", "Moldova", "Serbia", "Ukraine");
        addBorders(graph, "New Zealand", "Australia");
        addBorders(graph, "Russia", "Azerbaijan", "Belarus", "China", "Estonia", "Finland", "Georgia", "Kazakhstan",
                   "North Korea", "Latvia", "Lithuania", "Mongolia", "Norway", "Poland", "Ukraine");
        addBorders(graph, "Rwanda", "Burundi", "Democratic Republic of the Congo", "Tanzania", "Uganda");
        addBorders(graph, "San Marino", "Italy");
        addBorders(graph, "Saudi Arabia", "Iraq", "Jordan", "Kuwait", "Oman", "Qatar", "United Arab Emirates", "Yemen");

        WORLD = graph;
    }

    @Test
    void testWorld() {
        assertNotNull(WORLD);
        assertFalse(WORLD.isEmpty());
        assertEquals(NATIONS.length, WORLD.numberOfVertices());
        assertTrue(WORLD.numberOfEdges() > 0);
        assertTrue(WORLD.density() > 0.0);

        // examples of querying the graph
        assertTrue(WORLD.vertices().map(WORLD::neighbours).anyMatch(Set::isEmpty));
        assertTrue(WORLD.vertices().map(WORLD::neighbours).map(Set::size).anyMatch(isEqual(1)));
    }

    @Test
    void testAllBordersBiDirectional() {
        assertTrue(WORLD.vertices().map(
                source -> WORLD.neighbours(source).stream()
                               .map(WORLD::neighbours)
                               .allMatch(neighbours -> neighbours.contains(source))
        ).allMatch(isEqual(true)));
    }

    @Test
    void findTriangles() {
        // silly test to find sets of three countries each of which borders both of the other two
        // such as {Poland,Belarus,Ukraine}
        var triangles = WORLD.vertices().map(WorldMap::trianglesOf)
                             .flatMap(Set::stream)
                             .collect(Collectors.toSet());
        assertNotNull(triangles);
        System.out.println("Triangles found: " + triangles.size());
        triangles.stream()
                 .map(ArrayList::new)
                 .peek(Collections::sort)
                 .map(List::toString)
                 .sorted()
                 .forEach(System.out::println);

    }

    static Set<Set<String>> trianglesOf(String a) {
        return WORLD.neighbours(a)
                    .stream()
                    .map(b -> trianglesOf(a, b))
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());
    }

    static Set<Set<String>> trianglesOf(String a, String b) {
        return WORLD.neighbours(a).stream()
                    .filter(WORLD.neighbours(b)::contains)
                    .map(c -> Arrays.asList(a, b, c))
                    .map(HashSet::new)
                    .collect(Collectors.toSet());
    }
}
