package ukf;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/eventDetailServlet")
public class eventDetailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection con;
    private String error = "";

    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/hotel_events", "root", "");
        } catch (Exception e) {
            error = e.getMessage();
        }
    }

    public void destroy() {
        try { if (con != null) con.close(); } catch (Exception e) {}
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        createHeader(out, request);
        createBody(out, request);
        createFooter(out, request);
    }

    private void createHeader(PrintWriter out, HttpServletRequest request) {
        HttpSession ses = request.getSession(false);
        String meno = ses != null ? (String) ses.getAttribute("first_name") : null;
        String priezvisko = ses != null ? (String) ses.getAttribute("last_name") : null;
        String email = ses != null ? (String) ses.getAttribute("login") : null;

        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1'>");
        out.println("<title>Eventy</title>");
        out.println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
        out.println("</head>");

        out.println("<nav style='background-color: #1a1a1a; color: white; padding: 15px; display: flex; align-items: center; justify-content: space-between;'>");

        out.println("<div style='display: flex; align-items: center;'>");
        out.println("<a class='navbar-brand mb-0 h1 me-3' href='mainServlet' style='text-decoration: none; color: white;'>Eventy</a>");
        out.println("<form action='mainServlet' method='get'>");
        out.println("<input type='text' name='query' placeholder='Hľadať eventy' style='padding: 5px; border-radius: 5px; border: none;'>");
        out.println("</form></div>");

        out.println("<a href='hotelServlet' style='color: white; text-decoration: none; font-size: 16px;'>Zobraziť hotely</a>");

        out.println("<div style='position: relative; display: inline-block;'>");

        if (ses != null && ses.getAttribute("ID") != null) {
            String inic = meno.substring(0, 1).toUpperCase() + priezvisko.substring(0, 1).toUpperCase();

            out.println("<button onclick='toggleMenu()' style='background: none; border: none; color: white; font-size: 16px; cursor: pointer;'>");
            out.println("<div style='display: flex; align-items: center; gap: 10px;'>");
            out.println("<div style='width: 40px; height: 40px; background-color: #555; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: bold;'>");
            out.println(inic);
            out.println("</div></div></button>");

            out.println("<div id='profileMenu' style='display: none; position: absolute; right: 0; background: white; color: black; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2); border-radius: 5px; padding: 10px; min-width: 200px;'>");
            out.println("<p style='font-weight: bold;'>" + meno + " " + priezvisko + "</p>");
            out.println("<p style='color: gray; font-size: 14px;'>" + email + "</p>");

            if (RoleHelper.hasAccess(ses, "admin", "staff")) {
                out.println("<a href='myEventServlet' style='display: block; padding: 5px 0;'>Moje eventy</a>");
                out.println("<a href='pridatEventServlet' style='display: block; padding: 5px 0;'>Pridať Event</a>");
            }
            if (RoleHelper.hasAccess(ses, "admin")) {
                out.println("<a href='zmenitRoleServlet' style='display: block; padding: 5px 0;'>Zmeniť role</a>");
            }

            out.println("<a href='upravProfilServlet' style='display: block; padding: 5px 0;'>Upraviť profil</a>");
            out.println("<a href='mojeRezervacieServlet' style='display: block; padding: 5px 0;'>Moje rezervácie</a>");
            out.println("<form action='mainServlet' method='post'>");
            out.println("<input type='hidden' name='operacia' value='logout'>");
            out.println("<button type='submit' style='background: none; border: none; color: red; padding: 5px 0;'>Odhlásiť sa</button>");
            out.println("</form>");
            out.println("</div>");
        } else {
            out.println("<a href='loginServlet' style='color: white; text-decoration: none; padding: 8px 16px; border: 1px solid white; border-radius: 5px;'>Prihlásiť sa</a>");
        }

        out.println("</div></nav>");
        out.println("<script>");
        out.println("function toggleMenu() {");
        out.println("  var menu = document.getElementById('profileMenu');");
        out.println("  menu.style.display = menu.style.display === 'block' ? 'none' : 'block';");
        out.println("}");
        out.println("</script>");
    }

    private void createBody(PrintWriter out, HttpServletRequest request) {
        String idStr = request.getParameter("eventId");

        if (idStr != null) {
            try {
                int eventId = Integer.parseInt(idStr);
                String sql = "SELECT * FROM event WHERE id = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, eventId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    out.println("<div class='container mt-5'>");
                    String rezervaciaMsg = request.getParameter("rezervacia");
                    String zrusenieMsg = request.getParameter("zrusenie");

                    if ("ok".equals(rezervaciaMsg)) {
                        out.println("<div class='alert alert-success'>Rezervácia prebehla úspešne.</div>");
                    } else if ("ok".equals(zrusenieMsg)) {
                        out.println("<div class='alert alert-warning'>Rezervácia bola úspešne zrušená.</div>");
                    }

                    out.println("<h2>" + rs.getString("event_name") + "</h2>");
                    out.println("<p><strong>Organizátor:</strong> " + rs.getString("organizer_company") + "</p>");
                    out.println("<p><strong>Dátum:</strong> " + rs.getString("event_date") + "</p>");
                    String description = rs.getString("description");
                    String[] parts = description.split(";");
                    out.println("<p><strong>Popis:</strong></p>");
                    out.println("<ul>");
                    for (String part : parts) {
                        if (!part.trim().isEmpty()) {
                            out.println("<li>" + part.trim() + "</li>");
                        }
                    }
                    out.println("</ul>");
                    int dostupnost = rs.getInt("available");
                    if (dostupnost == 1) {
                        out.println("<p><strong>Dostupnosť:</strong> Voľné miesta!</p>");
                    } else {
                        out.println("<p><strong>Dostupnosť:</strong> Všetky miesta vypredané!</p>");
                    }

                    HttpSession ses = request.getSession(false);
                    if (ses != null && ses.getAttribute("ID") != null) {
                        int currentUserId = (int) ses.getAttribute("ID");
                        String role = RoleHelper.getUserRole(ses);
                        int creatorId = rs.getInt("user_id");

                        boolean maRezervaciu = false;

                        try {
                            String checkSql = "SELECT * FROM event_reservation WHERE user_id = ? AND event_id = ?";
                            PreparedStatement checkPs = con.prepareStatement(checkSql);
                            checkPs.setInt(1, currentUserId);
                            checkPs.setInt(2, eventId);
                            ResultSet checkRs = checkPs.executeQuery();
                            maRezervaciu = checkRs.next(); 
                            checkRs.close();
                            checkPs.close();
                        } catch (SQLException e) {
                            out.println("<p class='text-danger'>Chyba pri kontrole rezervácie: " + e.getMessage() + "</p>");
                        }

                        out.println("<div class='mt-4'>");

                        if (!maRezervaciu) {
                            if (dostupnost == 1) {
                                out.println("<form method='post' action='rezervujEventServlet' style='display: inline;'>");
                                out.println("<input type='hidden' name='event_id' value='" + eventId + "'>");
                                out.println("<button type='submit' class='btn btn-primary me-2'>Rezervovať</button>");
                                out.println("</form>");
                            } else {
                                out.println("<p class='text-danger fw-bold'>Event je momentálne plne obsadený. Rezervácia nie je možná.</p>");
                            }
                        } else {
                            out.println("<form method='post' action='zrusRezervaciuServlet' style='display: inline;'>");
                            out.println("<input type='hidden' name='event_id' value='" + eventId + "'>");
                            out.println("<button type='submit' class='btn btn-outline-danger me-2'>Zrušiť rezerváciu</button>");
                            out.println("</form>");
                        }


                        if (RoleHelper.hasAccess(ses, "admin") || currentUserId == creatorId) {
                            out.println("<form method='post' action='vymazEventServlet' style='display: inline;' onsubmit='return confirm(\"Naozaj chcete vymazať tento event?\");'>");
                            out.println("<input type='hidden' name='event_id' value='" + eventId + "'>");
                            out.println("<button type='submit' class='btn btn-danger me-2'>Vymazať</button>");
                            out.println("</form>");
                        }

                        if (RoleHelper.hasAccess(ses, "admin") || currentUserId == creatorId) {
                        	out.println("<form method='get' action='upravEventServlet' style='display: inline;'>");
                        	out.println("<input type='hidden' name='event_id' value='" + eventId + "'>");
                        	out.println("<button type='submit' class='btn btn-warning me-2'>Upraviť</button>");
                        	out.println("</form>");
                        }

                        out.println("</div>");
                    }
                    out.println("</div>");

                } else {
                    out.println("<div class='container mt-5'><p class='text-danger'>Event sa nenašiel.</p></div>");
                }
                rs.close();
                ps.close();
            } catch (Exception e) {
                out.println("<div class='container mt-5'><p class='text-danger'>Chyba: " + e.getMessage() + "</p></div>");
            }
        } else {
            out.println("<div class='container mt-5'><p class='text-warning'>Nešpecifikovaný eventId.</p></div>");
        }
    }

    private void createFooter(PrintWriter out, HttpServletRequest request) {
        out.println("<footer class='footer py-3 bg-dark text-white text-center' style='position: fixed; bottom: 0; width: 100%;'>");
        out.println("<p class='mb-0'>© 2024 Event Management</p>");
        out.println("</footer>");

        out.println("<script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js'></script>");

        out.println("<style>");
        out.println("html, body { height: 100%; margin: 0; padding: 0; display: flex; flex-direction: column; }");
        out.println(".container { flex: 1; padding-bottom: 60px; }");
        out.println("footer { height: 60px; }");
        out.println("</style>");
    }
}
