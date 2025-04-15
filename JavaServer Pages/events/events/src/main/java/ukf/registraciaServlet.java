package ukf;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/registraciaServlet")
public class registraciaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection con;

    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/hotel_events", "root", "");
        } catch (Exception e) {
            throw new ServletException("Nepodarilo sa pripojiť na databázu", e);
        }
    }

    public void destroy() {
        try { if (con != null) con.close(); } catch (Exception e) {}
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String operacia = request.getParameter("operacia");
        
        if ("register".equals(operacia)) {
            zobrazFormular(response.getWriter());
        } else if ("registracia".equals(operacia)) {
            spracujRegistraciu(request, response);
        }
    }
    


    private void zobrazFormular(PrintWriter out) {
        out.println("<!DOCTYPE html><html lang='sk'><head>");
        out.println("<meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1'>");
        out.println("<title>Registrácia</title>");
        out.println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
        out.println("<style>");
        out.println("body { background-color: #f8f9fa; display: flex; justify-content: center; align-items: center; height: 100vh; }");
        out.println(".login-container { background: white; padding: 30px; border-radius: 10px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); width: 100%; max-width: 500px; }");
        out.println(".btn-custom { background-color: #007bff; color: white; border: none; }");
        out.println(".btn-custom:hover { background-color: #0056b3; }");
        out.println("</style></head><body>");

        out.println("<div class='login-container'>");
        out.println("<h3 class='text-center mb-4'>Registrácia</h3>");
        out.println("<form action='registraciaServlet' method='post'>");

        out.println("<div class='mb-3'><label class='form-label'>Meno</label><input type='text' class='form-control' name='meno' required></div>");
        out.println("<div class='mb-3'><label class='form-label'>Priezvisko</label><input type='text' class='form-control' name='priezvisko' required></div>");
        out.println("<div class='mb-3'><label class='form-label'>Dátum narodenia</label><input type='date' class='form-control' name='born'></div>");
        out.println("<div class='mb-3'><label class='form-label'>Telefón</label><input type='text' class='form-control' name='phone'></div>");
        out.println("<div class='mb-3'><label class='form-label'>Email</label><input type='email' class='form-control' name='email' required></div>");
        out.println("<div class='mb-3'><label class='form-label'>Heslo</label><input type='password' class='form-control' name='heslo' required></div>");
        out.println("<div class='mb-3'><label class='form-label'>Pohlavie</label>");
        out.println("<select name='gender' class='form-control'><option value='M'>Muž</option><option value='F'>Žena</option></select></div>");

        out.println("<input type='hidden' name='operacia' value='registracia'>");
        out.println("<button type='submit' class='btn btn-custom w-100'>Registrovať</button>");
        out.println("</form></div>");

        out.println("<script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js'></script>");
        out.println("</body></html>");
    }

    private void spracujRegistraciu(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String meno = request.getParameter("meno");
        String priezvisko = request.getParameter("priezvisko");
        String born = request.getParameter("born");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String heslo = request.getParameter("heslo");
        String gender = request.getParameter("gender");

        try {
            PreparedStatement overenie = con.prepareStatement("SELECT COUNT(*) FROM user WHERE email = ?");
            overenie.setString(1, email);
            ResultSet rs = overenie.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                out.println("Tento email sa už používa.");
                return;
            }

            String hashHeslo = BCrypt.hashpw(heslo, BCrypt.gensalt());

            String sql = "INSERT INTO user (first_name, last_name, born, phone, email, password, gender, role, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, meno);
            ps.setString(2, priezvisko);
            ps.setDate(3, (born != null && !born.isEmpty()) ? Date.valueOf(born) : null);
            ps.setString(4, phone);
            ps.setString(5, email);
            ps.setString(6, hashHeslo);
            ps.setString(7, gender);
            ps.setString(8, "guest");

            Timestamp now = Timestamp.valueOf(LocalDateTime.now());
            ps.setTimestamp(9, now);
            ps.setTimestamp(10, now);

            int vlozene = ps.executeUpdate();
            if (vlozene > 0) {
            	response.sendRedirect("loginServlet?registracia=ok");
            } else {
                out.println("Chyba pri registrácii.");
            }

        } catch (Exception e) {
            out.println("Chyba: " + e.getMessage());
        }
    }
}
