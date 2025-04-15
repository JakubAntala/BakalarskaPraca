package ukf;

import java.io.*;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/loginServlet")
public class loginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection con;
    private String error = "";

    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel_events", "root", "");
        } catch (Exception e) {
            error = "Nepodarilo sa pripojiť k databáze: " + e.getMessage();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        zobrazLoginFormular(response, null);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (con == null || error.length() > 0) {
            zobrazLoginFormular(response, error);
            return;
        }

        String login = request.getParameter("login");
        String pwd = request.getParameter("pwd");

        try {
            PreparedStatement pstmt = con.prepareStatement(
                "SELECT * FROM user WHERE email = ?"
            );
            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String hashedPwd = rs.getString("password");
                if (BCrypt.checkpw(pwd, hashedPwd)) {
                    HttpSession session = request.getSession();
                    session.setAttribute("ID", rs.getInt("id"));
                    session.setAttribute("login", rs.getString("email"));
                    session.setAttribute("first_name", rs.getString("first_name"));
                    session.setAttribute("last_name", rs.getString("last_name"));
                    session.setAttribute("born", rs.getDate("born"));
                    session.setAttribute("phone", rs.getString("phone"));
                    session.setAttribute("email", rs.getString("email"));
                    session.setAttribute("role", rs.getString("role"));

                    response.sendRedirect("mainServlet");
                    return;
                } else {
                    zobrazLoginFormular(response, "Nesprávne heslo.");
                }
            } else {
                zobrazLoginFormular(response, "Používateľ s týmto emailom neexistuje.");
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            zobrazLoginFormular(response, "Chyba pri overovaní používateľa: " + e.getMessage());
        }
    }

    private void zobrazLoginFormular(HttpServletResponse response, String chybovaHlaska) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html><html lang='sk'><head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1'>");
        out.println("<title>Prihlásenie</title>");
        out.println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
        out.println("<style>");
        out.println("body { background-color: #f8f9fa; min-height: 100vh; display: flex; flex-direction: column; }");
        out.println(".login-container { max-width: 400px; margin: auto; margin-top: 50px; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }");
        out.println("</style></head><body>");

        out.println("<nav style='background-color: #1a1a1a; color: white; padding: 15px; display: flex; justify-content: space-between;'>");
        out.println("<div><a href='mainServlet' style='color: white; text-decoration: none; margin-right: 20px;'>Eventy</a>");
        out.println("<a href='hotelServlet' style='color: white; text-decoration: none;'>Hotely</a></div>");
        out.println("<div><a href='loginServlet' style='color: white; text-decoration: none; border: 1px solid white; padding: 5px 10px; border-radius: 5px;'>Prihlásiť sa</a></div></nav>");

        out.println("<div class='login-container'>");
        out.println("<h3 class='text-center mb-4'>Prihlásenie</h3>");
        if (chybovaHlaska != null) {
            out.println("<div class='alert alert-danger'>" + chybovaHlaska + "</div>");
        }
        out.println("<form action='loginServlet' method='post'>");
        out.println("<div class='mb-3'><label for='login' class='form-label'>Login</label>");
        out.println("<input type='text' class='form-control' name='login' required></div>");
        out.println("<div class='mb-3'><label for='pwd' class='form-label'>Heslo</label>");
        out.println("<input type='password' class='form-control' name='pwd' required></div>");
        out.println("<button type='submit' class='btn btn-primary w-100'>Prihlásiť</button>");
        out.println("</form><hr>");
        out.println("<form action='registraciaServlet' method='post'>");
        out.println("<input type='hidden' name='operacia' value='register'>");
        out.println("<button class='btn btn-outline-success w-100'>Registrovať</button>");
        out.println("</form></div>");

        out.println("<script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js'></script>");
        out.println("<script>");
        out.println("const params = new URLSearchParams(window.location.search);");
        out.println("if (params.get('registracia') === 'ok') {");
        out.println("    alert('Registrácia prebehla úspešne.');");
        out.println("}");
        out.println("if (params.get('logout') === 'ok') {");
        out.println("    alert('Boli ste úspešne odhlásený.');");
        out.println("}");
        out.println("</script>");

        out.println("</body></html>");
    }
}
