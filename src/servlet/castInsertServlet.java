package servlet;

import dao.CastDao;
import dao.PersonDao;
import entity.Cast;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Person;

@WebServlet("/newCastMovie")
public class castInsertServlet extends HttpServlet {

    public static class castPerson{
        public int cast_id = 0;
        public Integer movie_id = 0;
        public Integer actor_id = 0;
        public String character_name = "";

        public int getCast_id() {
            return cast_id;
        }

        public Integer getMovie_id() {
            return movie_id;
        }

        public Integer getActor_id() {
            return actor_id;
        }

        public String getCharacter_name() {
            return character_name;
        }

        public int getOrder_of_appearance() {
            return order_of_appearance;
        }

        public String getName() {
            return name;
        }

        public int getGender() {
            return gender;
        }

        public int order_of_appearance = 0;
        public String name = "";
        public int gender = 0;
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        int movie_id = Integer.valueOf(request.getParameter("movie_id"));
        int cast_id = Integer.valueOf(request.getParameter("cast_id"));

        movieCastServlet.castPerson cp = new movieCastServlet.castPerson();
        cp.actor_id = 0;
        cp.cast_id = cast_id;
        cp.movie_id = movie_id;
        cp.order_of_appearance = 0;
        cp.character_name = "";
        cp.name = "";
        cp.gender = 0;


        request.setAttribute("castPerson", cp);


        request.getRequestDispatcher("insertByMovieIDAndPersonID.jsp").forward(request,response);

    }
}