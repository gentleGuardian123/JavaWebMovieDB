import SQLTemplate.*;
import dao.MoviesDao;
import dao.TableName;
import entity.Cast;
import entity.Crew;
import entity.Genre;
import entity.GenreMovie;
import entity.Movie;
import util.SQLUtil;

import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;

public class SQLTemplateTest {
    public final static String table = "test";
    public final static String anotherTable = "anotherTest";
    public static void main(String[] args) {
//        TestInsertT();
//        TestSelectTSimple();
//        TestUpdateT();
//        TestDeleteT();
//        TestSelectTJoin();
        TestSelectTJoinWithExtraConditions_2();
    }

    static class TestDao {
        public Integer a1;
        public int a2;
        public double a3;
        public String a4;
        public String a5;
        public Date a6;

        public TestDao() {}
        public TestDao(Integer a1, int a2, double a3, String a4, String a5, Date a6) {
            this.a1 = a1;
            this.a2 = a2;
            this.a3 = a3;
            this.a4 = a4;
            this.a5 = a5;
            this.a6 = a6;
        }

        /** Getters which return the attribute name, attribute type in MySQL, and the exact value. */
        public SQLUtil.DataInfo getA1() {
            return new SQLUtil.DataInfo("a1", SQLUtil.DataType.BIGINT, this.a1);
        }
        public SQLUtil.DataInfo getA2() {
            return new SQLUtil.DataInfo("a2", SQLUtil.DataType.INT, this.a2);
        }
        public SQLUtil.DataInfo getA3() {
            return new SQLUtil.DataInfo("a3", SQLUtil.DataType.DECIMAL, this.a3);
        }
        public SQLUtil.DataInfo getA4() {
            return new SQLUtil.DataInfo("a4", SQLUtil.DataType.VARCHAR, this.a4);
        }
        public SQLUtil.DataInfo getA5() {
            return new SQLUtil.DataInfo("a5", SQLUtil.DataType.TEXT, this.a5);
        }
        public SQLUtil.DataInfo getA6() {
            return new SQLUtil.DataInfo("a6", SQLUtil.DataType.DATE, this.a6);
        }

        /** Setters. */
        public void setA1(Integer a1) {
            this.a1 = a1;
        }
        public void setA2(int a2) {
            this.a2 = a2;
        }
        public void setA3(double a3) {
            this.a3 = a3;
        }
        public void setA4(String a4) {
            this.a4 = a4;
        }
        public void setA5(String a5) {
            this.a5 = a5;
        }
        public void setA6(String a6) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            try {
                this.a6 = df.parse(a6);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    static class AnotherTestDao {
        public Integer a7;
        public Integer a1;
        public String a8;

        public AnotherTestDao() {}
        public AnotherTestDao(Integer a7, Integer a1, String a8) {
            this.a7 = a7;
            this.a1 = a1;
            this.a8 = a8;
        }

        public SQLUtil.DataInfo getA7() {
            return new SQLUtil.DataInfo("a7", SQLUtil.DataType.BIGINT, this.a7);
        }
        public SQLUtil.DataInfo getA1() {
            return new SQLUtil.DataInfo("a1", SQLUtil.DataType.BIGINT, this.a1);
        }
        public SQLUtil.DataInfo getA8() {
            return new SQLUtil.DataInfo("a8", SQLUtil.DataType.VARCHAR, this.a8);
        }

        public void setA7(Integer a7) {
            this.a7 = a7;
        }
        public void setA1(Integer a1) {
            this.a1 = a1;
        }
        public void setA8(String a8) {
            this.a8 = a8;
        }
    }

    public static void TestInsertT() {
        /** Test whether the insert SQL is correctly generated by Template.*/
        TestDao td = new TestDao(1234567890, 100, 30.36,
                "Hello, world", "Hello, my name is Alice, nice to meet you.",
                new Date(123, Calendar.DECEMBER, 3));
        String expected = """
                INSERT INTO\s
                test(a1, a2, a3, a4, a5, a6)\s
                VALUES(1234567890, 100, 30.36, 'Hello, world', 'Hello, my name is Alice, nice to meet you.', '2023-12-03');""";
        String actual = new InsertT(table)
                .AddKeyValuePair(td.getA1())
                .AddKeyValuePair(td.getA2())
                .AddKeyValuePair(td.getA3())
                .AddKeyValuePair(td.getA4())
                .AddKeyValuePair(td.getA5())
                .AddKeyValuePair(td.getA6())
                .toSQL();
        assertEquals(expected, actual);

        /** Test whether the execution result is successful.*/
        int count = SQLUtil.Update(actual);
        assertTrue( count > 0);
    }

    public static void TestDeleteT() {
        /** Test whether the insert SQL is correctly generated by Template.*/
        String expected = """
                DELETE FROM\s
                test\s
                WHERE a1 = 1234567890;""";
        String actual = new DeleteT(table)
                .AddCondition("a1 = 1234567890")
                .toSQL();
        assertEquals(expected, actual);

        /** Test whether the execution result is successful.*/
        int count = SQLUtil.Update(actual);
        assertTrue( count > 0);
    }

    public static void TestUpdateT() {
        /** Test whether the insert SQL is correctly generated by Template.*/
        TestDao td = new TestDao();
        td.setA2(200);
        td.setA4("hello world");
        String expected = """
                UPDATE test\s
                SET a2 = 200, a4 = 'hello world'\s
                WHERE a1 = 1234567890;""";
        String actual = new UpdateT(table)
                .AddCondition("a1 = 1234567890")
                .AddKeyValuePair(td.getA2())
                .AddKeyValuePair(td.getA4())
                .toSQL();
        assertEquals(expected, actual);

        /** Test whether the execution result is successful.*/
        int count = SQLUtil.Update(actual);
        assertTrue( count > 0);
    }

    public static void TestSelectTSimple() {
        String expected = """
                SELECT *\s
                FROM test\s
                WHERE a1 = 1234567890;""";
        String actual = new SelectT(table)
                .AddCondition("a1 = 1234567890")
                .toSQL();
        assertEquals(expected, actual);
    }

    public static void TestSelectTJoin() {
        String expected = """
                SELECT test.a1, a2, a6, a7, a8\s
                FROM test, anotherTest\s
                WHERE test.a1 = anotherTest.a1;""";
        String actual = new SelectT(List.of(table, anotherTable))
                .AddColumn(table, new TestDao().getA1().attri_name)
                .AddColumn(new TestDao().getA2().attri_name)
                .AddColumn(new TestDao().getA6().attri_name)
                .AddColumn(new AnotherTestDao().getA7().attri_name)
                .AddColumn(new AnotherTestDao().getA8().attri_name)
                .AddCondition(new Condition(Condition.Opt.E,
                        table, new TestDao().getA1().attri_name,
                        anotherTable, new AnotherTestDao().getA1().attri_name))
                .toSQL();
        assertEquals(expected, actual);

    }

    public static void TestSelectTJoinWithExtraConditions_1() {
        int page = 1;
        int genre_id = 35;
        String expected = """
                SELECT MOVIES.*\s
                FROM MOVIES, GENRES, MOVIE_GENRES\s
                WHERE MOVIES.id = MOVIE_GENRES.movie_id AND GENRES.id = MOVIE_GENRES.genre_id AND GENRES.id = 35\s
                ORDER BY popularity DESC, release_date DESC\s
                LIMIT 0, 20;""";
        String actual = new SelectT(List.of(TableName.movie_table, TableName.genre_table, TableName.movie_genre_table))
                .AddColumn(TableName.movie_table, "*")
                .AddOrder(new Movie().Popularity().attri_name, SelectT.OrderType.DESC)
                .AddOrder(new Movie().ReleaseDate().attri_name, SelectT.OrderType.DESC)
                .Limit((page-1) * 20, 20)
                .AddCondition(new Condition(Condition.Opt.E,
                        TableName.movie_table, new Movie().MovieId().attri_name,
                        TableName.movie_genre_table, new GenreMovie().MovieId().attri_name))
                .AddCondition(new Condition(Condition.Opt.E,
                        TableName.genre_table, new Genre().Id().attri_name,
                        TableName.movie_genre_table, new GenreMovie().GenreId().attri_name))
                .AddCondition(new Condition(Condition.Opt.E, TableName.genre_table,
                        new Genre().Id().attri_name, String.valueOf(genre_id)))
                .toSQL();
        assertEquals(expected, actual);

        /** Test whether the Query result is not null.*/
        List<Movie> ml = MoviesDao.QueryAndResolve(actual);
        System.out.printf("|%-50s\t|popularity\t\t|revenue\t\t|release_date\t\t\t\t\t\t|\n", "title");
        System.out.println(new String(new char[121]).replace('\0', '-'));
        for (Movie m : ml) {
            System.out.printf("|%-50s\t|%.6f\t\t|%d\t\t|%s\t\t|\n", m.title, m.popularity, m.revenue, m.release_date);
        }
    }

    public static void TestSelectTJoinWithExtraConditions_2() {
        int personId = 35;
        Cast wanted = new Cast();
        wanted.setActorId(personId);
        Crew orWanted = new Crew();
        orWanted.setCrewMemberId(personId);
        String expected = """
                (\s
                SELECT MOVIES.*\s
                FROM MOVIES, CAST\s
                WHERE MOVIES.id = CAST.movie_id AND actor_id = 35\s
                ORDER BY popularity DESC, release_date DESC\s
                )\s
                UNION\s
                (\s
                SELECT MOVIES.*\s
                FROM MOVIES, CREW\s
                WHERE MOVIES.id = CREW.movie_id AND crew_member_id = 35\s
                ORDER BY popularity DESC, release_date DESC\s
                );""";
        String actual =
                SelectT.Union(List.of(
                        new SelectT(List.of(TableName.movie_table, TableName.cast_table))
                                .AddColumn(TableName.movie_table, "*")
                                .AddCondition(new Condition(Condition.Opt.E,
                                        TableName.movie_table, new Movie().MovieId().attri_name,
                                        TableName.cast_table, new Cast().MovieId().attri_name))
                                .AddCondition(new Condition(Condition.Opt.E, wanted.ActorId())),
                        new SelectT(List.of(TableName.movie_table, TableName.crew_table))
                                .AddColumn(TableName.movie_table, "*")
                                .AddCondition(new Condition(Condition.Opt.E,
                                        TableName.movie_table, new Movie().MovieId().attri_name,
                                        TableName.crew_table, new Crew().MovieId().attri_name))
                                .AddCondition(new Condition(Condition.Opt.E, orWanted.CrewMemberId()))
                ));
//       assertEquals(expected, actual);

        /** Test whether the Query result is not null.*/
        List<Movie> ml = MoviesDao.QueryAndResolve(actual);
        System.out.printf("|%-50s\t|popularity\t\t|revenue\t\t|release_date\t\t\t\t\t\t|\n", "title");
        System.out.println(new String(new char[121]).replace('\0', '-'));
        for (Movie m : ml) {
            System.out.printf("|%-50s\t|%.6f\t\t|%d\t\t|%s\t\t|\n", m.title, m.popularity, m.revenue, m.release_date);
        }
    }
}
