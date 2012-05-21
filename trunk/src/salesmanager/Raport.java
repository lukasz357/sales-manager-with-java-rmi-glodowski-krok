/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package salesmanager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import salesmanager.GenericMap.GenericCity;
import salesmanager.GeneticAlgorithm.Costs;

/**
 *
 * @author Łukasz
 */
public class Raport {

    private final String path;
    private final String title;
    private BufferedWriter out;
    private FileWriter fstream;
    ArrayList<GenericCity>[] routes;
    ArrayList<Driver> drivers;
    HashMap<String, Integer> products;
    HashMap<GenericCity, ArrayList<Product>> zamowienia;
    private Costs k;

    public Raport(ArrayList<GenericCity>[] routes, ArrayList<Driver> drivers,
            HashMap<GenericCity, ArrayList<Product>> zamowienia,Costs k, String path, String title) {
        this.path = path;
        this.title = title;
        this.routes = routes;
        this.drivers = drivers;
        this.zamowienia = zamowienia;
        this.k = k;
        products = new HashMap<String, Integer>(30);
        for (ArrayList<Product> p : zamowienia.values()) {
            for(Product pr : p){
                if(products.containsKey(pr.name)){
                    int tmp = products.get(pr.name);
                    products.put(pr.name, tmp);
                }
                else
                    products.put(pr.name, pr.count);
            }
        }
        try {
            fstream = new FileWriter(path);
            out = new BufferedWriter(fstream);
        } catch (IOException ex) {
            System.out.println("Nie ma takiego pliku: " + path);
            System.exit(1);
        }
    }

    public void saveHTMLReport() throws IOException {
        int totalSum = 0;
        out.write("<html>\n"
                + "<head>\n"
                + "<title>" + this.title + "</title>\n"
                + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />"
                + "<style>\n.trasa{\nmax-width:400px;vertical-align:top;\n}\n"
                + ".rowspan{\nvertical-align:top;\n}\n"
                + "body{\nborder:2px solid black; width:800px; margin-left: auto; margin-right:"
                + "auto; margin-top:10px;\n}\n</style>"
                + "</head>\n"
                + "<body style=\"border:2px solid black; width:800px; margin-left: auto; "
                + "margin-right: auto; margin-top:10px;\">\n");
        out.write("<center><h2>" + this.title + "</h2></center>\n");
        out.write("<div style=\"margin-left:10px;\">\n");
       
        out.write("<center><table border='2'>");
        out.write("<tr>\n<th class='kierowca'>Kierowca</th>\n<th class='trasa'>Trasa</th>"
                + "<th class='miasto'>Miasto</th>\n<th class='produkt'>Produkt</th>\n<th class='ilosc'>Ilość</th>\n</tr>");
        for (int i = 0; i < routes.length; i++) {
            String trasa = new String(); int suma=0;
            for (int j = 0; j < routes[i].size(); j++) {
                trasa+=routes[i].get(j).getVal() + (j < routes[i].size() - 1 ? " -> " : "");
            }

            for(GenericCity c : routes[i]){
                ArrayList<Product> a= zamowienia.get(c);
                if(a == null)
                    continue;
                suma += a.size();
            }
            out.write("<tr>\n<td class='rowspan' rowspan='" + suma + "'>" +drivers.get(i).getName()
                    +"</td>\n<td class='trasa' rowspan='"+ suma +"'>"+trasa+"</td>");
            
            for (GenericCity c : routes[i]) {
                if (zamowienia.get(c) == null){ //to oznacza, że do miasta z którego startujemy
                    continue;                 //nie ma zamówien ( a mogą być )
                }
                String name = c.getVal();
                
                out.write("<td class='rowspan' rowspan='"+ zamowienia.get(c).size() +"'>");
                out.write(name);
                out.write("</td>");
                ArrayList<Product> stuff = zamowienia.get(c);
                for (Product prd : stuff) {
                    out.write("<td>" +prd.name+"</td><td>"+prd.count+"</td>\n");
                    suma += prd.count;
                    out.write("</tr><tr>");
                }

            }
            out.write("<tr><td colspan='5' align='right'>Suma: "+ suma +". Długość trasy: " + policzDlTrasy(routes[i])+ "</td></tr>");
            totalSum+=suma;
            suma = 0;
        }
        out.write("</table></center>\n");
        out.write("<p style=\"font-variant: small-caps; font-size: 11pt; fontweight: bold; font-family: Verdana\">");
        out.write("<h3>SPRZEDANE PRZEDMIOTY:</h3>");
        out.write("</p>\n");
        out.write("<table style=\"margin-left:80px;\">\n");
        for(java.util.Map.Entry<String, Integer> e : products.entrySet()){
            out.write("<tr>\n");
            out.write("<td style=\"font-variant: small-caps; width: 140px; padding-left: 20px; font-size: 11pt; fontweight: bold; font-family: Verdana\">");
            out.write("-" + e.getKey());
            out.write("</td>");

            out.write("<td>");
            out.write(e.getValue() + " szt.");
            out.write("</td>\n");
            out.write("</tr>\n");
        }
        out.write("<tr><td align='right' colspan='2'>Łącznie: " + totalSum + " szt.</td></tr>\n</table>");
        out.write("</div>\n");
        out.write("</body>\n");
        out.write("</html>\n");
        out.close();
    }

    private double policzDlTrasy(ArrayList<GenericCity> trasa){
        double dl = 0;
        for(int i = 0; i<trasa.size()-1;i++)
            dl+=k.lengths[trasa.get(i).getID()][trasa.get(i+1).getID()];
        dl+=k.lengths[trasa.get(trasa.size()-1).getID()][trasa.get(0).getID()];
        return dl;
    }

}
