package jihanki;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class Tsurisen {

	public static final String TYPE = "補充するお金の種類";
	public static final String MOUNT = "補充するお金の量";
	public final static String THOUSAND_YEN = "1000yen";
	public final static String FIVE_HUNDRED_YEN ="500yen";
	public final static String HUNDRED_YEN ="100yen";
	public final static String FIFTEEN_YEN ="50yen";
	public final static String TEN_YEN = "10yen";
	BufferedReader br = null;

	/**
	 * @author t-murase
	 * @param br
	 * このプログラムではよくreadLineを使うので、使うたびにBufferedReaderのインスタンス作るはどうかと思うので、
	 * 一つのインスタンスを使い回すことにした
	 */
	public Tsurisen(BufferedReader br) {
		this.br = br;
	}

	/**
	 * @author t-murase
	 * 現在自動販売機にあるお金の中で、残りの小銭が10枚以下になっているものがあるかどうか確認
	 */
	public boolean isTsurisenEmpty(){
		JihankiAction ja = new JihankiAction();
		DBConnection dbcon = new DBConnection();
	    List<MoneyBean> MoneyList = dbcon.getMoneies();
	    for(MoneyBean mb :MoneyList) {
	       	if(mb.getCount() <= 10) {
	       		while(ja.userAuthment()) {
	       			return true;
	       		}
        	}
	    }
        return false;
	}

	/**
	 * @author t-murase
	 * @return このメソッドがうまく動いたかどうかを返す
	 * 釣り銭の補充を行う
	 */

	public boolean chargeTsurisen() {
		boolean succsess = true;
        DBConnection dbcon = new DBConnection();
        System.out.println(TYPE);
        String moneyName = null;
        int moneyCount =  0;
		try {
			moneyName = br.readLine();
	        System.out.println(MOUNT);
	        moneyCount = Integer.parseInt(br.readLine());
		} catch (IOException e) {
			e.printStackTrace();
		}
    	dbcon.setCoinsCount(moneyName, moneyCount);
        List<MoneyBean> Coins = dbcon.getMoneies();
        for(int i = 0; i < Coins.size();i++) {
        	System.out.println(Coins.get(i).getMoneyName());
        	System.out.println(Coins.get(i).getCount());
        	if(Coins.get(i).getCount() <= 10) {
        		chargeTsurisen();
        		break;
        	}
        }
		return  succsess;
	}

	/**
	 * @author t-murase
	 * @param ja
	 * 商品購入の際、利用者が入れたお金を自動販売機の中に入れる
	 */
	public void plusTsurisen(JihankiAction ja) {
		DBConnection dbcon = new DBConnection();
		dbcon.plusMoneies(ja.thousand_yen_mount, THOUSAND_YEN);
		dbcon.plusMoneies(ja.five_hundred_yen_mount, FIVE_HUNDRED_YEN);
		dbcon.plusMoneies(ja.hundred_yen_mount,HUNDRED_YEN);
		dbcon.plusMoneies(ja.fifteen_yen_mount,FIFTEEN_YEN);
		dbcon.plusMoneies(ja.ten_yen_mount,TEN_YEN);
	}

	/**
	 * @author t-murase
	 * @param ja
	 * お釣りでお金を出した時のDB処理。お釣りとして出す必要のあるお金を出す
	 */
	public void minousTsurisen(JihankiAction ja) {
		DBConnection dbcon = new DBConnection();
		dbcon.minousMoneies(ja.thousand_yen_mount, THOUSAND_YEN);
		dbcon.minousMoneies(ja.five_hundred_yen_mount, FIVE_HUNDRED_YEN);
		dbcon.minousMoneies(ja.hundred_yen_mount,HUNDRED_YEN);
		dbcon.minousMoneies(ja.fifteen_yen_mount,FIFTEEN_YEN);
		dbcon.minousMoneies(ja.ten_yen_mount,TEN_YEN);
	}

	/**
	 * @author t-murase
	 * @param ja
	 * 釣り銭としてどんなお金をどのくらい出すかを計算する処理
	 */
	public void calcTsurisen(JihankiAction ja){
		ja.thousand_yen_mount = ja.Balance / 1000;
		ja.Balance = ja.Balance % 1000;
		ja.five_hundred_yen_mount = ja.Balance / 500;
		ja.Balance = ja.Balance % 500;
		ja.hundred_yen_mount = ja.Balance / 100;
		ja.Balance = ja.Balance % 100;
		ja.fifteen_yen_mount = ja.Balance / 50;
		ja.Balance = ja.Balance % 50;
		ja.ten_yen_mount = ja.Balance / 10;
		ja.Balance = ja.Balance % 10;
	}

	/**
	 *
	 * @param password
	 * @param user_name
	 * @return
	 * ユーザログインの処理。
	 * ログイン成功時にtrueを返す
	 */
	public boolean userLogin(String password,String user_name) {
		DBConnection dbcon = new DBConnection();
		return dbcon.getPass(dbcon.hashPass(password), user_name);
	}
}

