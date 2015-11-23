package com.company.defaultparser;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.sun.tools.javac.jvm.ClassFile;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.xml.sax.Attributes;


public class Parsing {

    public static void main(String[] args) throws Exception {

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        String sql = "";
        String url = "jdbc:postgresql://localhost/dmdprojectdb";
        String user = "bbr";
        String password = "88";

        InputStream data = new FileInputStream("dblp.xml");
        Digester digester = new Digester();

        digester.addObjectCreate("dblp", ArrayList.class); //4 //TODO OBJECT==1;
        digester.addObjectCreate("dblp/book", ArrayList.class); //3 //TODO BOOKS==0;
        digester.addObjectCreate("dblp/article", ArrayList.class); //2 ss
        digester.addObjectCreate("dblp/proceedings", ArrayList.class); //2 ss
        digester.addObjectCreate("dblp/inproceedings", ArrayList.class); //2 ss
        digester.addObjectCreate("dblp", HashMap.class);
        digester.addObjectCreate("dblp", ArrayList.class);
        digester.addObjectCreate("dblp", ArrayList.class);
        digester.addObjectCreate("dblp", HashMap.class);

        //Calling add method while encountering author xpath

        ProceedingRule rule = new ProceedingRule();

        digester.addRule("dblp/proceedings/year", rule);
        digester.addRule("dblp/proceedings/author", rule);
        digester.addRule("dblp/proceedings/editor", rule);
        digester.addRule("dblp/proceedings/series", rule);
        digester.addRule("dblp/proceedings/title", rule);
        digester.addRule("dblp/proceedings/booktitle", rule);
        digester.addRule("dblp/proceedings/ee", rule);
        digester.addRule("dblp/proceedings/journal", rule);
        digester.addRule("dblp/proceedings/publisher", rule);
        digester.addRule("dblp/proceedings/volume", rule);
        digester.addRule("dblp/proceedings/isbn", rule);
        digester.addRule("dblp/proceedings", rule);

        digester.addRule("dblp/article/year", rule);
        digester.addRule("dblp/article/author", rule);
        digester.addRule("dblp/article/editor", rule);
        digester.addRule("dblp/article/series", rule);
        digester.addRule("dblp/article/title", rule);
        digester.addRule("dblp/article/number", rule);
        digester.addRule("dblp/article/volume", rule);
        digester.addRule("dblp/article/pages", rule);
        digester.addRule("dblp/article/booktitle", rule);
        digester.addRule("dblp/article/ee", rule);
        digester.addRule("dblp/article/journal", rule);
        digester.addRule("dblp/article", rule);


        digester.addRule("dblp/book/year", rule);
        digester.addRule("dblp/book/author", rule);
        digester.addRule("dblp/book/editor", rule);
        digester.addRule("dblp/book/series", rule);
        digester.addRule("dblp/book/title", rule);
        digester.addRule("dblp/book/publisher", rule);
        digester.addRule("dblp/book/pages", rule);
        digester.addRule("dblp/book/volume", rule);
        digester.addRule("dblp/book/isbn", rule);
        digester.addRule("dblp/book/ee", rule);
        digester.addRule("dblp/book/journal", rule);
        digester.addRule("dblp/book", rule);
        digester.addRule("dblp", rule);

        digester.addRule("dblp/inproceedings/year", rule);
        digester.addRule("dblp/inproceedings/series", rule);
        digester.addRule("dblp/inproceedings/author", rule);
        digester.addRule("dblp/inproceedings/editor", rule);
        digester.addRule("dblp/inproceedings/title", rule);
        digester.addRule("dblp/inproceedings/booktitle", rule);
        digester.addRule("dblp/inproceedings/ee", rule);
        digester.addRule("dblp/inproceedings/journal", rule);
        digester.addRule("dblp/inproceedings", rule);

        ArrayList resultList = (ArrayList) digester.parse(data);
        ArrayList<Book> books = (ArrayList) resultList.get(0);
        ArrayList<Article> articles = (ArrayList) resultList.get(1);
        ArrayList<Proceeding> proceedings = (ArrayList) resultList.get(2);
        ArrayList<Inproceeding> inproceedings = (ArrayList) resultList.get(3);
        HashSet<Person> persons = (HashSet) resultList.get(4);
        ArrayList<Papers> paperses = (ArrayList) resultList.get(6);
        ArrayList<HashMap<String, ArrayList<Person>>> writtenRelations = (ArrayList) resultList.get(5);
        HashMap<Integer, String> idName = (HashMap) resultList.get(7);

        for (Map.Entry<Integer, String> entry : idName.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }

        System.out.println("==============Hashing started....================");
        Map<String, Integer> getAuthorId = new HashMap<>();
        persons.stream().forEach(person -> getAuthorId.put(person.getName(), person.getId()));
        System.out.println("==============Hashing finished====================");
        System.out.println(getAuthorId.size() + " persons have been hashed.");
        System.out.println("==============Creating 'written' relations....================");
        try (Connection c = DriverManager.getConnection(url, user, password); Statement s = c.createStatement()) {
            Map<Integer, String> writtenRelation = new HashMap<>();
            Iterator<HashMap<String, ArrayList<Person>>> it = writtenRelations.iterator();
            int counter = 0;
            while (it.hasNext()) {
                for (Map.Entry<String, ArrayList<Person>> entry : it.next().entrySet()) {
                    insertWritten("Written", c, entry.getKey(), entry.getValue(), getAuthorId);
                    counter++;
                }
                switch (counter) {
                    case 200:
                        System.out.println(counter + " insertions.");
                        break;
                    case 1000:
                        System.out.println(counter + " insertions.");
                        break;
                    case 10000:
                        System.out.println(counter + " insertions.");
                        break;
                    case 50000:
                        System.out.println(counter + " insertions.");
                        break;
                }
            }
            System.out.println("==============Relations created====================");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try (Connection c = DriverManager.getConnection(url, user, password); Statement s = c.createStatement()) {

           /* System.out.println("==============Written==============");
            for (int i = 0; i < writtenRelations.size(); i++) {
                //idName.entrySet().stream().forEach(e -> insertWritten("Written", c, e.getKey(), e.getValue(), idName));
                Map<String, ArrayList<Person>> relation = writtenRelations.get(i);
                for (Map.Entry<String, ArrayList<Person>> entry : relation.entrySet()) {
                    insertWritten("Written", c, entry.getKey(), entry.getValue(), idName);
                }
                if (i > 500000) {
                    System.out.println("500000 entries");
                }
                if (i > 1000000) {
                    System.out.println("1000000 entries");
                }
                if (i > 2000000) {
                    System.out.println("2000000 entries");
                }
                if (i > 3000000) {
                    System.out.println("3000000 entries");
                }
            }
            System.out.println("==============INSERTION COMPLETED==============");*/


            System.out.println("==============Authors==============");
            System.out.println("author's size: " + persons.size());
            for (Person person : persons) {
                insertAuthors("Authors", c, person);
            }
            System.out.println("==============INSERTION COMPLETED==============");

            System.out.println("==============Papers==============");

            Iterator<Papers> iterator = paperses.iterator();
            while (iterator.hasNext()) {
                insertPapers("Papers", c, iterator.next());
            }

            System.out.println("==============INSERTION COMPLETED==============");

            System.out.println("==============Written==============");
            String sqlInsert = "INSERT INTO WRITTEN (AUTHORID, KEY) VALUES ";
            for (int i = 0; i < writtenRelations.size(); i++) {
                switch (i) {
                    case 10000: {
                        System.out.println(i);
                        break;
                    }
                    case 50000: {
                        System.out.println(i);
                        break;
                    }
                    case 150000: {
                        System.out.println(i);
                        break;
                    }
                }
                //idName.entrySet().stream().forEach(e -> insertWritten("Written", c, e.getKey(), e.getValue(), idName));
                Map<String, ArrayList<Person>> relation = writtenRelations.get(i);
                for (Map.Entry<String, ArrayList<Person>> entry : relation.entrySet()) {
                    //sqlInsert += insertWritten("Written", c, entry.getKey(), entry.getValue(), idName);
                }
                if (i > 500000) {
                    System.out.println("500000 entries");
                }
                if (i > 1000000) {
                    System.out.println("1000000 entries");
                }
                if (i > 2000000) {
                    System.out.println("2000000 entries");
                }
                if (i > 3000000) {
                    System.out.println("3000000 entries");
                }
            }
            System.out.println(sqlInsert.length() + " length of the query!");
            sqlInsert = sqlInsert.substring(0, sqlInsert.length() - 1);
            System.out.println("new length: " + sqlInsert.length());

            System.out.println("==============INSERTION COMPLETED==============");

            System.out.println("==============Books==============");
            System.out.println("There are " + books.size() + " books.");
            int counter = 0;
            for (Book book : books) {
                insertIt("Books", c, book);
                counter++;
            }
            System.out.println(counter + " of these books have been inserted.");
            System.out.println("==============INSERTION COMPLETED==============");

            System.out.println("==============Articles==============");
            System.out.println("There are " + articles.size() + " articles.");
            int counter_ = 0;
            for (Article article : articles) {
                insertIt("Articles", c, article);
                counter_++;
            }
            System.out.println(counter + " of these articles have been inserted.");
            System.out.println("==============INSERTION COMPLETED==============");

            System.out.println("==============Proceedings==============");
            for (Proceeding proceeding : proceedings) {
                insertIt("Proceedings", c, proceeding);
            }
            System.out.println("==============INSERTION COMPLETED==============");

            System.out.println("==============Inproceedings==============");
            for (Inproceeding inproceeding : inproceedings) {
                insertIt("Inproceedings", c, inproceeding);
            }
            System.out.println("==============INSERTION COMPLETED==============");


        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(ClassFile.Version.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(ClassFile.Version.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }

        /*System.out.println("number of persons is: " + persons.size());
        *//*for (Person person : persons) {
            System.out.println(person.getName());
        }*//*
        System.out.println("=============================");
        System.out.println("written relations: " + writtenRelations.size());


        System.out.println("__________________");
        System.out.println("TEST");
        System.out.println(paperses.size() + " papers");
        System.out.println(articles.size() + " articles");
        System.out.println(books.size() + " books");
        System.out.println(proceedings.size() + " proceedings");
        System.out.println(inproceedings.size() + " inproceedings");
        System.out.println("all the papers minus all other tables = " + (paperses.size() - articles.size() - books.size() - proceedings.size() - inproceedings.size()));
        System.out.println("__________________");
        //for (int i = 0; i < writtenRelations.size(); i++) {
        for (int i = 0; i < 20; i++) {
            Map<String, ArrayList<Person>> relation = writtenRelations.get(i);
            //Map.Entry relationEntry = (Map.Entry) relation.entrySet();
            for (Map.Entry<String, ArrayList<Person>> entry : relation.entrySet()) {
                System.out.println("the title: " + entry.getKey());
                ArrayList<Person> persons_ = (ArrayList) entry.getValue();
                System.out.println("authors: ");
                for (Person person : persons_) {
                    System.out.println(person.getName());
                }
            }
        }*/
        //System.out.println("proceedings: " + inproceedings.get(0).getCrossref() + "  " + inproceedings.get(0).getBooktitle());
//        System.out.println(articles.get(0).getPages() + " " + articles.get(0).getJournal() + " " + articles.get(0).getVolume());
//        System.out.println("this fcking stack... " + books.get(0).getPages() + " " + books.get(0).getPublisher()+ " " + books.get(0).getIsbn());

        /*System.out.println(digester.getCount() + " rule digester") ;
        //ArrayList<Book> booksList = (ArrayList) digester.peek(0);
        System.out.println(digester.getCount()+ "SCACSACASCASC");
        System.out.println(digester.peek(0).getClass());*/
        /*System.out.println(parsedData.size() + "  sizeeee");
        System.out.println(parsedData.get(0).getClass());
        System.out.println(booksList.size() + " FKING PAGES!");
        //ArrayList books1 = (ArrayList) parsedData.get(0);
        Book books_ = (Book) parsedData.get(0);*/

        //System.out.println(books_.getPages());

//        ((ArrayList) digester.parse(data)).get(0);
        //System.out.println(books_.size());
        //System.out.println(books_.get(0).getPages());
        //ArrayList list = (ArrayList) digester.peek("written");
        //System.out.println(list.getClass());
        //digester.peek("persons");
        //digester.peek("");
        /*while (dataitr.hasNext()) {
            Papers proceeding = dataitr.next();
            System.out.println(proceeding.getTitle() + ";   " + proceeding.getEditor() + ";   " + proceeding.getSeries() + ";   " + proceeding.getmDate());
        }*/
        //Iterator<Entry<String, ArrayList>> dataItr = parsedData.entrySet().iterator();
        /*Iterator<ArrayList<Papers>> dataItr = parsedData.iterator();
        while(dataItr.hasNext()){
            *//*Entry<String, ArrayList> entry = dataItr.next();
            System.out.println("Title : " + entry.getKey() + ", Authors" + entry.getValue().toString());*//*
            ArrayList<Papers> proceeding = dataItr.next();
        }*/
        try (Connection c = DriverManager.getConnection(url, user, password)) {
        } catch (SQLException ex) {

        }
        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();

            /*while (rs.next()) {
                System.out.println(rs.getString(1));//+ "   " + rs.getString(2));// + "   " + rs.getInt(3));// + "   " + rs.getInt(4));
            }*/

            /*sql = "CREATE TABLE Authors " +
                    "(AuthorID int NOT NULL, " +
                    "Name varchar NOT NULL, " +
                    "Lab varchar, " +
                    "University varchar, " +
                    "PRIMARY KEY (AuthorID) " +
                    ");";

            st.executeUpdate(sql);
            System.out.println("Table created.");

            sql = "CREATE TABLE Papers " +
                    "(Key varchar NOT NULL, " +
                    "title varchar NOT NULL, " +
                    "type varchar NOT NULL, " +
                    "year int, " +
                    "mdate varchar NOT NULL, " +
                    "url varchar, " +
                    "PRIMARY KEY (Key) " +
                    ");";
            st.executeUpdate(sql);
            System.out.println("Table created.");

            sql = "CREATE TABLE Written " +
                    "(AuthorID int REFERENCES AUTHORS (AUTHORID), " +
                    "Key varchar REFERENCES PAPERS (KEY), " +
                    "PRIMARY KEY (AuthorID, Key) " +
                    "); ";
            st.executeUpdate(sql);
            System.out.println("Table created.");

            sql = "CREATE TABLE Keywords " +
                    "(ID SERIAL NOT NULL, " +
                    "word varchar NOT NULL, " +
                    "PRIMARY KEY (ID) " +
                    ");";
            st.executeUpdate(sql);
            System.out.println("Table created.");

            sql = "CREATE TABLE Contains " +
                    "(ID int REFERENCES KEYWORDS (ID), " +
                    "Key varchar REFERENCES PAPERS (KEY), " +
                    "PRIMARY KEY (ID, Key) " +
                    ");";
            st.executeUpdate(sql);

            sql = "CREATE TABLE Books " +
                    "(Key varchar NOT NULL REFERENCES PAPERS (KEY), " +
                    "series varchar, " +
                    "isbn varchar, " +
                    "publisher varchar, " +
                    "volume int, " +
                    "type varchar, " +
                    "pages int, " +
                    "PRIMARY KEY (Key) " +
                    "); ";
            st.executeUpdate(sql);
            System.out.println("Table created.");

            sql = "CREATE TABLE Articles " +
                    "(Key varchar NOT NULL REFERENCES PAPERS (KEY), " +
                    "series varchar, " +
                    "journal varchar, " +
                    "volume varchar, " +
                    "number int, " +
                    "pages int, " +
                    "PRIMARY KEY (Key) " +
                    ");";
            st.executeUpdate(sql);
            System.out.println("Table created.");

            sql = "CREATE TABLE Proceedings " +
                    "(Key varchar NOT NULL REFERENCES PAPERS (KEY), " +
                    "series varchar, " +
                    "booktitle varchar, " +
                    "isbn varchar, " +
                    "publisher varchar, " +
                    "volume int, " +
                    "PRIMARY KEY (Key) " +
                    ");";
            st.executeUpdate(sql);
            System.out.println("Table created.");

            sql = "CREATE TABLE Inproceedings " +
                    "(Key varchar NOT NULL REFERENCES PAPERS (KEY), " +
                    "series varchar, " +
                    "booktitle varchar, " +
                    "crossref varchar, " +
                    "pages int, " +
                    "PRIMARY KEY (Key) " +
                    ");";
            st.executeUpdate(sql);
            System.out.println("Table created.");*/

          /*  sql = "CREATE TABLE IF NOT EXISTS DBLP_RECORD " +
                    "(KEY              TEXT NOT NULL PRIMARY KEY, " +
                    "MDATE             TEXT NOT NULL, " +
                    "TITLE             TEXT NOT NULL, " +
                    "VENUE             TEXT , " +
                    "YEAR              TEXT , " +
                    "PAGES             TEXT, " +
                    "TYPE              TEXT NOT NULL, " +
                    "EE                TEXT)";*/

            /*sql = "CREATE TABLE IF NOT EXISTS DBLP_RECORD_ARTICLE " +
                    "(KEY              TEXT NOT NULL PRIMARY KEY, " +
                    "MDATE             TEXT NOT NULL, " +
                    "TITLE             TEXT NOT NULL, " +
                    "VENUE             TEXT , " +
                    "YEAR              TEXT , " +
                    "PAGES             TEXT, " +
                    "TYPE              TEXT NOT NULL, " +
                    "EE                TEXT)";*/

            //"EDITOR            TEXT , " +
            //"PUBLISHER         TEXT , " +
            //"BOOKTITLE         TEXT , " +
            //"VOLUME            TEXT , " +
            //"ISBN                TEXT , " +
            //"URL               TEXT , " +

            //"JOURNALS          TEXT)";

            /*Papers papers_;// = new Papers();
            while (dataitr.hasNext()) {
                papers_ = dataitr.next();
                System.out.println(papers_.getTitle());
                insertIt("DBLP_RECORD", con, papers_);
                //st.executeUpdate(sql);
            }*/
            st.close();
            con.close();

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(ClassFile.Version.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(ClassFile.Version.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }

    }

    private static void insertAuthors(String table, Connection con, Person author) throws SQLException {
        String sql = "INSERT INTO " + table.toUpperCase() + " (AUTHORID, NAME) " +
                "VALUES (?,?)";
        //System.out.println("is it legal? " + con.isClosed());
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setInt(1, author.getId());
        preparedStatement.setString(2, author.getName());
        preparedStatement.executeUpdate();

    }

    private static void insertPapers(String table, Connection con, Papers paper) throws SQLException {

        String sql = "INSERT INTO " + table.toUpperCase() + " (KEY, TITLE, TYPE, YEAR, MDATE, URL) " +
                "VALUES (?,?,?,?,?,?)";

        String year;
        if ((year = paper.getYear()) != null) {
        } else {
            year = "0";
        }

        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setString(1, paper.getKey());
        preparedStatement.setString(2, paper.getTitle());
        preparedStatement.setString(3, paper.getType());
        preparedStatement.setInt(4, Integer.parseInt(year));
        preparedStatement.setString(5, paper.getmDate());
        preparedStatement.setString(6, paper.getEe());

        preparedStatement.executeUpdate();

    }

    //private static void insertWritten(String table, Connection con, HashMap<String, ArrayList<Person>> written) {
    /*private static void insertWritten(String table, Connection con, String key, ArrayList<Person> persons) throws SQLException {
        String sql = "INSERT INTO " + table.toUpperCase() + " (AUTHORID, KEY) " +
                "VALUES (?,?)";

        ArrayList<Integer> authorIDs = new ArrayList<>();

        String sqlQuery = "SELECT AUTHORID FROM AUTHORS WHERE NAME = ?";
        PreparedStatement preparedStatement = con.prepareStatement(sqlQuery);
        for (Person person : persons) {
            preparedStatement.setString(1, person.getName());
            ResultSet rs = preparedStatement.executeQuery();
            //Statement st = con.createStatement();
            while (rs.next()) {
                authorIDs.add(rs.getInt(1));
            }
        }

        preparedStatement = con.prepareStatement(sql);
        for (Integer iDs : authorIDs) {
            preparedStatement.setInt(1, iDs);
            preparedStatement.setString(2, key);
            preparedStatement.executeUpdate();
        }
    }*/
    // private static String insertWritten(String table, Connection con, String key_, List<Person> persons, Map<Integer, String> idName) throws SQLException {

    private static String insertWritten(String table, Connection con, String key_, List<Person> persons, Map<String, Integer> idName) throws SQLException {
        String sql = "INSERT INTO " + table.toUpperCase() + " (AUTHORID, KEY) " +
                "VALUES (?,?)";
        ArrayList<Integer> authorIDs = new ArrayList<>();
        //int counter = 0;

        String sql_ = "";
        //sql_.append("INSERT INTO WRITTEN (AUTHORID, KEY) VALUES ");

        PreparedStatement preparedStatement = con.prepareStatement(sql);

        //TODO insert into aNumber (id) values (564),(43536),(34560)

        //preparedStatement.setArray(con.createArrayOf("int", );
        for (Person person : persons) {
            //authorIDs.add(idName.entrySet().stream().forEach(e -> e.getValue().compareTo(person.getName()) == 0 ? e.getKey():));
            authorIDs.add(idName.get(person.getName()));
            /*for (Map.Entry<Integer, String> id : idName.entrySet()) {
                if (person.getName().equals(id.getValue())) {
                    authorIDs.add(id.getKey());
                }*/
        }
        for (Integer iDs : authorIDs) {
            //sql_ += ("(" + iDs + ",'" + key_ + "'),");
            preparedStatement.setInt(1, iDs);
            preparedStatement.setString(2, key_);
            preparedStatement.executeUpdate();
        }

        //preparedStatement.executeUpdate();

       /* //TODO uncomment this expressions:
        for (Integer iDs : authorIDs) {
            preparedStatement.setInt(1, iDs);
            preparedStatement.setString(2, key_);
            preparedStatement.executeUpdate();
        }*/
        //return sql_.toString();
        return "";
    }

    private static int checkPages(String pages) {
        try {
            return Integer.parseInt(pages);
        } catch (NumberFormatException ex) {
            String temp = "";
            for (int i = 0; i < pages.length(); i++) {
                if (Character.isDigit(pages.charAt(i))) {
                    temp += pages.charAt(i);
                }
            }
            return temp.length() > 0 ? Integer.parseInt(temp) : 0;
        }
    }

    private static String insertIt(String table, Connection con, Papers proceeding) throws SQLException {
        String sql = "";

        if (proceeding instanceof Article) {
            sql = "INSERT INTO " + table.toUpperCase() + " (JOURNAL,NUMBER,VOLUME,PAGES, KEY) " + "VALUES (?,?,?,?,?)";

            PreparedStatement preparedStatement = con.prepareStatement(sql);
            String pages;
            if ((pages = ((Article) proceeding).getPages()) != null) {
                if (pages.contains("-")) {
                    pages = pages.substring(pages.lastIndexOf("-") + 1);
                }
                if (pages.contains("/")) {
                    pages = pages.split("/")[0];
                }
                if (pages.length() > 0) {
                    int page = 0;
                    page = checkPages(pages);
                    if (page > 0) {
                        preparedStatement.setInt(4, checkPages(pages));
                    } else {
                        preparedStatement.setNull(4, Types.INTEGER);
                    }
                } else {
                    preparedStatement.setNull(4, Types.INTEGER);
                }
            } else {
                preparedStatement.setNull(4, Types.INTEGER);
            }

            String number;
            if ((number = ((Article) proceeding).getNumber()) != null) {
                if (number.contains("/")) {
                    number = number.split("/")[0];
                }
                if (number.contains("-")) {
                    number = number.split("-")[0];
                }
                number = number.split("&")[0];
                int number_ = checkPages(number);

                preparedStatement.setInt(2, number_);
            } else {
                preparedStatement.setNull(2, Types.INTEGER);
            }
            String volume;
            if ((volume = ((Article) proceeding).getVolume()) != null) {
                if (volume.contains("-")) {
                    volume = volume.split("-")[0];
                }
                int volume_ = checkPages(volume);
                preparedStatement.setInt(3, volume_);
            } else {
                preparedStatement.setNull(3, Types.INTEGER);
            }

            preparedStatement.setString(1, ((Article) proceeding).getJournal());
            /*preparedStatement.setInt(2, Integer.parseInt(number));
            preparedStatement.setInt(3, Integer.parseInt(volume));
            preparedStatement.setInt(4, Integer.parseInt(pages));*/
            preparedStatement.setString(5, ((Article) proceeding).getKey());
            preparedStatement.executeUpdate();
            return null;
        }

        if (proceeding instanceof Book) {
            sql = "INSERT INTO " + table.toUpperCase() + " (SERIES,ISBN,VOLUME,PAGES,PUBLISHER,KEY) " + "VALUES (?,?,?,?,?,?)";

            PreparedStatement preparedStatement = con.prepareStatement(sql);
            String pages;
            if ((pages = ((Book) proceeding).getPages()) != null) {
                //checkPages(pages);
                //pages = pages.substring(pages.lastIndexOf("-") + 1);
                int page = checkPages(pages);
                if (page > 0) {
                    preparedStatement.setInt(4, page);
                } else {
                    preparedStatement.setNull(4, Types.INTEGER);
                }
            } else {
                preparedStatement.setNull(4, Types.INTEGER);
            }

            String volume;
            if ((volume = ((Book) proceeding).getVolume()) != null) {
                volume = volume.substring(volume.lastIndexOf("-") + 1);
                if (volume.contains("/")) {
                    volume = volume.split("/")[0];
                }
                //volume = volume.substring(0, volume.lastIndexOf("/"));
                preparedStatement.setInt(3, Integer.parseInt(volume));
            } else {
                //volume = "0";
                preparedStatement.setNull(3, Types.INTEGER);
            }

            preparedStatement.setString(1, ((Book) proceeding).getSeries());
            preparedStatement.setString(2, ((Book) proceeding).getIsbn());
           /* preparedStatement.setInt(3, Integer.parseInt(volume));
            preparedStatement.setInt(4, Integer.parseInt(pages));*/
            preparedStatement.setString(5, ((Book) proceeding).getPublisher());
            preparedStatement.setString(6, ((Book) proceeding).getKey());
            preparedStatement.executeUpdate();
            return null;
        }

        if (proceeding instanceof Proceeding) {
            sql = "INSERT INTO " + table.toUpperCase() + " (BOOKTITLE,SERIES,VOLUME,PUBLISHER,ISBN,KEY) "
                    + "VALUES (?,?,?,?,?,?)";

            PreparedStatement preparedStatement = con.prepareStatement(sql);
            String volume;
            if ((volume = ((Proceeding) proceeding).getVolume()) != null) {
                if (volume.contains("-")) {
                    volume = volume.split("-")[1];
                }
                if (volume.contains(".")) {
                    volume = volume.split(".")[0]; //for values like this: abs/1002.4535; So, it'll be transformed into this value: abs/1002
                }
                preparedStatement.setInt(3, checkPages(volume));
            } else {
                preparedStatement.setNull(3, Types.INTEGER);
            }

            preparedStatement.setString(1, ((Proceeding) proceeding).getBooktitle());
            preparedStatement.setString(2, ((Proceeding) proceeding).getSeries());
            //preparedStatement.setInt(3, Integer.parseInt(volume));
            preparedStatement.setString(4, ((Proceeding) proceeding).getPublisher());
            preparedStatement.setString(5, ((Proceeding) proceeding).getIsbn());
            preparedStatement.setString(6, ((Proceeding) proceeding).getKey());
            preparedStatement.executeUpdate();
            return null;
        }

        if (proceeding instanceof Inproceeding) {
            sql = "INSERT INTO " + table.toUpperCase() + " (BOOKTITLE,PAGES,CROSSREF,KEY) " + "VALUES (?,?,?,?)";

            String pages;
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            if ((pages = ((Inproceeding) proceeding).getPages()) != null) {
                pages = pages.substring(pages.lastIndexOf("-") + 1);
                preparedStatement.setInt(2, Integer.parseInt(pages));
            } else {
                preparedStatement.setNull(2, Types.INTEGER);
            }

            preparedStatement.setString(1, ((Inproceeding) proceeding).getBooktitle());
            //preparedStatement.setInt(2, Integer.parseInt(pages));
            preparedStatement.setString(3, ((Inproceeding) proceeding).getCrossref());
            preparedStatement.setString(4, ((Inproceeding) proceeding).getKey());
            preparedStatement.executeUpdate();
            return null;
        }


        /*sql = "INSERT INTO " + table.toUpperCase() + " (KEY,MDATE,TITLE,EDITOR,SERIES,EE,URL,ISBN,VOLUME,BOOKTITLE,PUBLISHER,YEAR) "
                + "VALUES (" + proceeding.getTitle() +", '2015-02-24', 'Test Title', 'Testing editor', 'Some series', 'www.lolcho.com', 'http://testdomain.com', '9873-329-231-1', '2', 'the title of the book', 'test publisher', '2024');";
*/
        /*sql = "INSERT INTO " + table.toUpperCase() + " (KEY,MDATE,TITLE,EDITOR,SERIES,EE,URL,ISBN,VOLUME,BOOKTITLE,PUBLISHER,YEAR) "
                + "VALUES ('"+ proceeding.getKey() +"', '" +
                               proceeding.getmDate() + "', '" +
                               proceeding.getTitle() + "', '" +
                               proceeding.getEditor() + "', '" +
                               proceeding.getSeries() + "', '" +
                               proceeding.getEe() + "', '" +
                               proceeding.getUrl() + "', '" +
                               proceeding.getIsbn() + "', '" +
                               proceeding.getVolume() + "', '" +
                               proceeding.getBookTitle() + "', '" +
                               proceeding.getPublisher() + "', '" +
                               proceeding.getYear() + "');";*/

     /*   sql = "INSERT INTO " + table.toUpperCase() + " (KEY,MDATE,TITLE,EDITOR,SERIES,EE,URL,ISBN,VOLUME,BOOKTITLE,PUBLISHER,YEAR) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";*/

       /* sql = "INSERT INTO " + table.toUpperCase() + " (KEY,MDATE,TITLE,EE,YEAR,TYPE) "
                + "VALUES (?,?,?,?,?,?)";

        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setString(1, proceeding.getKey());
        preparedStatement.setString(2, proceeding.getmDate());
        preparedStatement.setString(3, proceeding.getTitle());
        //preparedStatement.setString(4, proceeding.getPages());
        //preparedStatement.setString(5, proceeding.getVenue());
       *//* preparedStatement.setString(4, proceeding.getEditor());
        preparedStatement.setString(5, proceeding.getSeries());*//*
        preparedStatement.setString(4, proceeding.getEe());
       *//* preparedStatement.setString(7, proceeding.getUrl());
        preparedStatement.setString(8, proceeding.getIsbn());
        preparedStatement.setString(9, proceeding.getVolume());
        preparedStatement.setString(10,proceeding.getBookTitle());
        preparedStatement.setString(11,proceeding.getPublisher());*//*
        preparedStatement.setString(5, proceeding.getYear());
        preparedStatement.setString(6, proceeding.getType());

        preparedStatement.executeUpdate();*/
      /*try {
            sql = "INSERT INTO PUBLIC." + table.toUpperCase() + " (KEY,MDATE,TITLE,EDITOR,SERIES,EE,URL,ISBN,VOLUME,BOOKTITLE,PUBLISHER,YEAR) "//,JOURNALS) "
                    + "VALUES (" + proceeding.getKey() + "," +
                    "\'" + proceeding.getmDate() + "\', " +
                    "\'" + proceeding.getTitle() + "\', " +
                    "\'" + proceeding.getEditor() + "\', " +
                    "\'" + proceeding.getSeries() + "\', " +
                    "\'" + proceeding.getEe() + "\', " +
                    "\'" + proceeding.getUrl() + "\', " +
                    "\'" + proceeding.getIsbn() + "\', " +
                    "\'" + proceeding.getVolume() + "\', " +
                    "\'" + proceeding.getBookTitle() + "\', " +
                    "\'" + proceeding.getPublisher() + "\', " +
                    "\'" + proceeding.getYear() + "\');";
                    //"\'" + proceeding.getJournal() + "\');";
        }
        catch (Exception e) {}*/
        return sql;
    }

    private static class ProceedingRule extends Rule {

        boolean insideProceedings = false;
        boolean insideInproceedings = false;
        boolean insideArticle = false;
        boolean insideBook = false;
        boolean insideThesis = false;

        String currentKey;

        //tuples for inherited tables;
        Book book = new Book();
        Article article = new Article();
        Proceeding proceeding = new Proceeding();
        Inproceeding inproceeding = new Inproceeding();

        //inherited tables
        List<Book> books = new ArrayList<>();
        List<Article> articles = new ArrayList<>();
        List<Proceeding> proceedings = new ArrayList<>();
        List<Inproceeding> inproceedings = new ArrayList<>();

        //super table
        List<Papers> papers = new ArrayList<>();
        Set<Person> persons = new HashSet<>();
        Set<String> personsNames = new HashSet<>();

        //'Written' relationship
        List<Map<String, List<Person>>> writtenRelations = new ArrayList<>();
        Map<String, List<Person>> writtenRelation = new HashMap<>();
        Set<String> authorsNames = new HashSet<>();
        List<Person> paperAuthors = new ArrayList<>();
        Map<String, Integer> writtenMap = new HashMap<>();

        //SQL query
        String sql = "";
        Papers paper = new Papers();


        @Override
        public void begin(String namespace, String name, Attributes attributes) throws Exception {

            //proceeding = new Papers();
            String theKey = attributes.getValue("key");
            String theDate = attributes.getValue("mdate");
            currentKey = theKey;
            //System.out.println(name);
            switch (name) {
                case "proceedings": {
                    paper.setKey(theKey);
                    paper.setmDate(theDate);
                    paper.setType(name);
                    insideProceedings = true;
                    insideArticle = false;
                    insideBook = false;
                    //insideIncollection = false;
                    //insideThesis = false;
                    insideInproceedings = false;
                    break;
                }
                case "inproceedings": {
                    paper.setKey(theKey);
                    paper.setmDate(theDate);
                    paper.setType(name);
                    insideProceedings = false;
                    insideArticle = false;
                    insideBook = false;
                    //insideIncollection = false;
                    //insideThesis = false;
                    insideInproceedings = true;
                    break;
                }

                case "article": {
                    paper.setKey(theKey);
                    paper.setmDate(theDate);
                    paper.setType(name);
                    insideProceedings = false;
                    insideArticle = true;
                    insideBook = false;
                    insideInproceedings = false;
                    break;
                }
                case "book": {
                    paper.setKey(theKey);
                    paper.setmDate(theDate);
                    paper.setType(name);
                    insideProceedings = false;
                    insideArticle = false;
                    insideBook = true;
                    //insideIncollection = false;
                    //insideThesis = false;
                    insideInproceedings = false;
                    break;
                }

            }
        }


        @Override
        public void body(String namespace, String name, String text) throws Exception {

            if (name.equals("author") || name.equals("editor")) {
                //persons.add(new Person(text));
                personsNames.add(text); //for "Persons" table
                authorsNames.add(text); //for "Written" relationship
                //paperAuthors.add(new Person(text));
                /*articleAuthors.add(name);
                authors.add(name);*/

            }

            if (name.equals("year")) {
                paper.setYear(text);
                //System.out.println("inside the \'year\' tag:  " + text);
                //return;
            }
            if ((insideInproceedings || insideProceedings) && name.equals("booktitle")) {
                paper.setVenue(text);
                //return;
            }
            if (insideArticle && name.equals("journals")) {
                paper.setVenue(text);
                //return;
            }
            if ((insideProceedings || insideBook) && name.equals("series")) {
                paper.setVenue(text);
                //return;
            }
            if (name.equals("volume")) {
                proceeding.setVolume(text);
                //return;
            }
            if (name.equals("ee")) {
                paper.setEe(text);
            }
            if (name.equals("title")) {
                paper.setTitle(text);
            }
            if (insideThesis && name.equals("school")) {
                //paper.setVenue(text);
            }


            if (insideBook) {
                //pages, series, publisher, isbn, volume;
                //ArrayList books = (ArrayList) digester.peek(6);
                //System.out.println("INSIDE THE BOOK: " + name);
                if (book.getKey() == null) {
                    book.setKey(paper.getKey());
                }

                switch (name) {
                    case "pages": {
                        book.setPages(text);
                        break;
                    }
                    case "series": {
                        book.setSeries(text);
                        break;
                    }
                    case "publisher": {
                        book.setPublisher(text);
                        break;
                    }
                    case "isbn": {
                        book.setIsbn(text);
                        break;
                    }
                    case "volume": {
                        book.setVolume(text);
                        break;
                    }
                }
            }

            if (insideArticle) {
                //pages, series, publisher, isbn, volume;
                //ArrayList books = (ArrayList) digester.peek(6);
                if (article.getKey() == null) {
                    article.setKey(paper.getKey());
                }
                switch (name) {
                    case "pages": {
                        article.setPages(text);
                        break;
                    }
                    case "journal": {
                        article.setJournal(text);
                        break;
                    }
                    case "number": {
                        article.setNumber(text);
                        break;
                    }
                    case "volume": {
                        article.setVolume(text);
                        break;
                    }
                }
            }

            //TODO fix names.
            if (insideProceedings) {
                //pages, series, publisher, isbn, volume;
                //ArrayList books = (ArrayList) digester.peek(6);
                if (proceeding.getKey() == null) {
                    proceeding.setKey(paper.getKey());
                }
                switch (name) {
                    case "booktitle": {
                        proceeding.setBooktitle(text);
                        break;
                    }
                    case "publisher": {
                        proceeding.setPublisher(text);
                        break;
                    }
                    case "series": {
                        proceeding.setSeries(text);
                        break;
                    }
                    case "volume": {
                        proceeding.setVolume(text);
                        break;
                    }
                    case "isbn": {
                        proceeding.setIsbn(text);
                        break;
                    }
                }
            }

            if (insideInproceedings) {
                //pages, series, publisher, isbn, volume;
                //ArrayList books = (ArrayList) digester.peek(6);
                if (inproceeding.getKey() == null) {
                    inproceeding.setKey(paper.getKey());
                }
                switch (name) {
                    case "booktitle": {
                        inproceeding.setBooktitle(text);
                        break;
                    }
                    case "pages": {
                        inproceeding.setPages(text);
                        break;
                    }
                    case "crossref": {
                        inproceeding.setCrossref(text);
                        break;
                    }
                }
            }

        }

        @Override
        public void end(String namespace, String name) throws Exception {
            //ArrayList _proceedings = (ArrayList) digester.peek(0);
            //ArrayList object = (ArrayList) digester.peek(1);
            //if (name.equals("proceedings"))
            /*ArrayList written = (ArrayList) digester.peek(2);
            HashSet authors = (HashSet) digester.peek(1); //TODO unique authors;
            ArrayList articleAuthors = (ArrayList) digester.peek(0);*/

            //TODO parse the article

            if (name.equals("dblp")) {
                   /* ArrayList books_ = (ArrayList) digester.peek(0);
                    books_ = books;*/

                persons.addAll(personsNames.stream().map(Person::new).collect(Collectors.toList()));

                int counter = 1;

                Map<Integer, String> idName = new HashMap<>();
                for (Person p : persons) {
                    idName.put(counter, p.getName());
                    p.setId(counter++);
                }

                ArrayList objects = (ArrayList) digester.peek(4);

                objects.add(books);
                objects.add(articles);
                objects.add(proceedings);
                objects.add(inproceedings);
                objects.add(persons);
                objects.add(writtenRelations);
                objects.add(papers);
                objects.add(idName);

            }

            if (name.equals("proceedings") || name.equals("article") || name.equals("book") || name.equals("inproceedings")) {

                if (insideBook) {
                    books.add(book);
                    book = new Book();
                    paperAuthors.addAll(authorsNames.stream().map(Person::new).collect(Collectors.toList()));
                    writtenRelation.put(paper.getKey(), paperAuthors);
                    writtenRelations.add(writtenRelation);
                    paperAuthors = new ArrayList<>();
                    authorsNames = new HashSet<>();
                    writtenRelation = new HashMap<>();

                }

                if (insideArticle) {
                    articles.add(article);
                    article = new Article();
                    paperAuthors.addAll(authorsNames.stream().map(Person::new).collect(Collectors.toList()));
                    writtenRelation.put(paper.getKey(), paperAuthors);
                    writtenRelations.add(writtenRelation);
                    paperAuthors = new ArrayList<>();
                    authorsNames = new HashSet<>();
                    writtenRelation = new HashMap<>();
                }

                if (insideInproceedings) {
                    inproceedings.add(inproceeding);
                    inproceeding = new Inproceeding();
                    paperAuthors.addAll(authorsNames.stream().map(Person::new).collect(Collectors.toList()));
                    writtenRelation.put(paper.getKey(), paperAuthors);
                    writtenRelations.add(writtenRelation);
                    //currentKey = "";
                    paperAuthors = new ArrayList<>();
                    authorsNames = new HashSet<>();
                    writtenRelation = new HashMap<>();
                }
                if (insideProceedings) {
                    proceedings.add(proceeding);
                    proceeding = new Proceeding();
                    paperAuthors.addAll(authorsNames.stream().map(Person::new).collect(Collectors.toList()));
                    writtenRelation.put(paper.getKey(), paperAuthors);
                    writtenRelations.add(writtenRelation);
                    paperAuthors = new ArrayList<>();
                    authorsNames = new HashSet<>();
                    writtenRelation = new HashMap<>();
                }

                papers.add(paper);
                paper = new Papers();

            }

        }
    }

    private static class AuthorRule extends Rule {
        String currentTitle = "";

        @Override
        public void body(String namespace, String name, String text) throws Exception {
            //HashMap object = (HashMap) digester.peek(1);
            ArrayList authors = (ArrayList) digester.peek(0);
            if (name.equals("title")) {
                currentTitle = text;
            } else if (name.equals("author")) {
                authors.add(text);
            }
        }

        @Override
        public void end(String namespace, String name) throws Exception {
            HashMap object = (HashMap) digester.peek(1);
            ArrayList authors = (ArrayList) digester.peek(0);
            if (name.equals("proceedings") || name.equals("article")) {
                object.put(currentTitle, authors);
            }
        }
    }
}

/*
(article|inproceedings|
        proceedings|book|incollection|
        phdthesis|mastersthesis|www)*/
