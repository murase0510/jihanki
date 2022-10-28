package jihanki;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBConnection {
    public static final String URL = "jdbc:mysql://localhost/jihanki?autoReconnect=true&useSSL=false";
    public static final String SQL1 = "select * from moneies;";
    public static final String SQL2 = "update moneies set count = ? where money_name = ?;";
    public static final String SQL3 = "select * from shouhin";
    public static final String SQL4 = "select * from card where card_name = ?;";
    public static final String SQL5 = "update shouhin set shouhin_count = ? where name = ?;";
    public static final String SQL6 = "select * from user where user_name = ?";
    public static final String SQL7 = "update shouhin set shouhin_count = shouhin_count - 1 where name = ?;";
    public static final String SQL8 = "update moneies set count = count + ? where money_name = ? ";
    public static final String SQL9 = "update moneies set count = count - ? where money_name = ? ";
    public static final String SQL10 = "update shouhin set shouhin_count = ? where name = ? ";

    public static final String USERNAME = "root";
    public static final String PASSWORD = "W|2a*v#7";

    public static final String COIN_NAME = "money_name";
    public static final String COIN_COUNT = "count";

    public static final String SHOUHIN_NAME = "name";
    public static final String SHOUHIN_COUNT = "shouhin_count";
    public static final String SHOUHIN_PRICE = "price";

    public static final String CARD_NAME = "card_name";
    public static final String CARD_CHARGE = "card_charge";

    public static final String USER_PASS = "user_pass";

    /**
     * @author t-murase
     * @return moneiesテーブルにある情報全て
     * moneiesテーブルにある情報を全て返す
     */
    public List<MoneyBean> getMoneies(){
        MoneyBean mb = new MoneyBean();
        List<MoneyBean> coinList = new ArrayList<MoneyBean>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            statement = connection.prepareStatement(SQL1);
            rs = statement.executeQuery();
            while(rs.next()) {
            	mb.setMoneyName(rs.getString(COIN_NAME));
            	mb.setCount(rs.getInt(COIN_COUNT));
            	coinList.add(mb);
            	mb = new MoneyBean();
            }
            connection.close();
            statement.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
				connection.close();
	            statement.close();
	            rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

        }
        return coinList;
    }

    /**
     *
     * @param moneyName
     * @param moneyCount
     * @return このメソッドの処理がうまくいったかどうか
     * 自動販売機の中にあるmoneyNameで指定したお金をmoneyCountに変更する
     */
    public boolean setCoinsCount(String moneyName, int moneyCount){
    	boolean succsess = true;
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            statement = connection.prepareStatement(SQL2);
            statement.setInt(1, moneyCount);
            statement.setString(2, moneyName);
            statement.executeUpdate();
            connection.close();
            statement.close();
        } catch (SQLException e) {
        	succsess = false;
            e.printStackTrace();
        } finally {
            try {
				connection.close();
	            statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

        }
        return succsess;
    }

    /**
     *
     * @returnshouhinテーブルにある情報全て
     * shouhinテーブルにある情報を全て返す
     */
    public List<shouhinBean> getshouhin(){
    	shouhinBean sb = new shouhinBean();
        List<shouhinBean> shouhinList = new ArrayList<shouhinBean>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            statement = connection.prepareStatement(SQL3);
            rs = statement.executeQuery();
            while(rs.next()) {
            		sb.setName(rs.getString(SHOUHIN_NAME));
            		sb.setPrice(rs.getInt(SHOUHIN_PRICE));
            		sb.setShouhinCount(rs.getInt(SHOUHIN_COUNT));
            		shouhinList.add(sb);
            		sb = new shouhinBean();
            }
            connection.close();
            statement.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
				connection.close();
	            statement.close();
	            rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
        }
        return shouhinList;
    }

    /**
    *
    * @param shouhinName
    * @param shouhinCount
    * @return このメソッドの処理がうまくいったかどうか
    * 自動販売機の中にあるshouhinNameで指定したお金をshouhinCountに変更する
    */
    public boolean setShouhinsCount(String shouhinName, int shouhinCount){
    	boolean succsess = true;
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            statement = connection.prepareStatement(SQL5);
            statement.setInt(1, shouhinCount);
            statement.setString(2, shouhinName);
            statement.executeUpdate();
            connection.close();
            statement.close();
        } catch (SQLException e) {
        	succsess = false;
            e.printStackTrace();
        } finally {
            try {
				connection.close();
	            statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

        }
        return succsess;
    }

    /**
     * @author t-murase
     * @param user_name
     * @param password
     * @return パスワード認証がうまくいったかどうか
     * パスワード認証を行う
     */
    public boolean getPass(String user_name,String password){
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        String get_pass = null;
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            statement = connection.prepareStatement(SQL6);
            statement.setString(1, user_name);
            rs = statement.executeQuery();
            while(rs.next()) {
            	get_pass = rs.getString(USER_PASS);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
				connection.close();
	            statement.close();
	            rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

        }
        return get_pass.equals(hashPass(password));
    }

    /**
     * @author t-murase
     * @param password
     * @return
     * 入力されたパスワードをハッシュ化する
     */
    public String hashPass(String password) {
        byte[] cipher_byte;
        StringBuilder sb = null;
        try{
                MessageDigest md = MessageDigest.getInstance("SHA-512");
                md.update(password.getBytes());
                cipher_byte = md.digest();
                sb = new StringBuilder(2 * cipher_byte.length);
                for(byte b: cipher_byte) {
                        sb.append(String.format("%02x", b&0xff) );
                }
        } catch (Exception e) {
                e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     *
     * @param ja
     * @return  このメソッドの処理がうまくいったかどうか
     */
    public boolean afterBuyZaiko(JihankiAction ja) {
    	boolean succsess = true;
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            statement = connection.prepareStatement(SQL7);
            statement.setString(1, ja.selected_shouhin);
            statement.executeUpdate();
            connection.close();
            statement.close();
        } catch (SQLException e) {
        	succsess = false;
            e.printStackTrace();
        } finally {
            try {
				connection.close();
	            statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

        }
        return succsess;
    }
    /**
     *
     * @param mount
     * @param money_type
     * @return このメソッドの処理がうまくいったかどうか
     * 指定したお金を指定した量自動販売機の釣り銭に追加する
     */
    public boolean plusMoneies(int mount,String money_type) {
    	boolean succsess = true;
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            statement = connection.prepareStatement(SQL8);
            statement.setInt(1, mount);
            statement.setString(2, money_type);
            statement.executeUpdate();
            connection.close();
            statement.close();
        } catch (SQLException e) {
        	succsess = false;
            e.printStackTrace();
        } finally {
            try {
				connection.close();
	            statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

        }
        return succsess;
    }

    /**
     *
     * @param mount
     * @param money_type
     * @return このメソッドの処理がうまくいったかどうか
     * 指定したお金を指定した量自動販売機の釣り銭から引く
     */
    public boolean minousMoneies(int mount,String money_type) {
    	boolean succsess = true;
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            statement = connection.prepareStatement(SQL9);
            statement.setInt(1, mount);
            statement.setString(2, money_type);
            statement.executeUpdate();
            connection.close();
            statement.close();
        } catch (SQLException e) {
        	succsess = false;
            e.printStackTrace();
        } finally {
            try {
				connection.close();
	            statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

        }
        return succsess;
    }

    /**
     *
     * @param shouhin_count
     * @param shouhin_name
     * @return このメソッドの処理がうまくいったかどうか
     * 指定した商品を指定した数だけ自動販売機に追加する
     */
    public boolean replenishShouhin(int shouhin_count,String shouhin_name) {
    	boolean succsess = true;
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            statement = connection.prepareStatement(SQL10);
            statement.setInt(1, shouhin_count);
            statement.setString(2, shouhin_name);
            statement.executeUpdate();
            connection.close();
            statement.close();
        } catch (SQLException e) {
        	succsess = false;
            e.printStackTrace();
        } finally {
            try {
				connection.close();
	            statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

        }
        return succsess;
    }
}
