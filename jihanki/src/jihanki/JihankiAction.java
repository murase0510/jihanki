package jihanki;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class JihankiAction {
	public final static String Y = "y";
	public final static String N = "n";
	public final static String THOUSAND_YEN = "千円札を";
	public final static String FIVE_HUNDRED_YEN ="500円玉が";
	public final static String HUNDRED_YEN ="100円玉が";
	public final static String FIFTEEN_YEN ="50円玉が";
	public final static String TEN_YEN = "10円玉が";
	public final static String MAI = "枚";
	public final static String MAI_DETA = "枚出てきました";
	public final static String DERUOTO = "ガコンッ";
	public final static String DETEKITA = "が出てきました";
	public final static String NO_CHANGE ="釣り銭切れ";
	public final static String BACKED_MONEY ="(入れたお金が返却された)";
	public final static String ZANDAKA ="残高";
	public final static String YEN_KANJI = "円";
	public final static String YEN = "yen";
	public final static String E_MONEY = "e";
	public final static String SAPICA_MONEY = "s";
	public final static String REPAYMENT = "repayment";
	public final static String KURO_MARU = "　●";
	public final static String SIRO_MARU = "　○";
	public final static String BATSU = "　　×";
	public final static String SHUT_DOWN = "shutdown";
	public final static String CHARGE_TSURISEN = "charge_tsurisen";
	public static final String USER ="user";
	public static final String PASS ="pass";
	public static final String OPEN_JIHANKI ="自動販売機の鍵を開ける(パスワードの入力)";
	public static final String CHARGE_SHOUHIN = "charge_shouhin";
	public static final String INPUT_SHOUHIN_MOUNT = "補充したい量を入力してください";
	public static final String SHOUHIN_HOJU = "(商品の補充を行います。補充したい商品名を入力してください。補充を終わりたいなら『end』と入力してください)";
	public static final String END_OF_HOJU = "end";
	public static final String WAIT_FOR_ACTION = "販売中";
	public static final String SHINSAI = "shinsai";
	public static final String WINNING_LOTTERY = "当たりが出たからもう一本";
	public static final String EMONEY_ZANDAKA = "電子マネー残高:";
	public static final String SAPICA_ZANDAKA = "SAPICA残高:";
	public static final String STOP_HANBAI = "販売停止";
	public static final String HOT = "あったか〜い";
	public static final String COLD = "つめた〜い";
	public static final String SPACE = " ";
	public static final int MAX_ZANDAKA = 7600;

	public String selected_shouhin = null;
	boolean can_add_balance_by_card = true;
	boolean can_add_balance_by_real_money = true;
	boolean can_add_balance_by_sapica = true;
	boolean sapica = false;
	boolean electronic = false;
	boolean shinsai = false;
	boolean winning_lottery = false;
	List<shouhinBean> shouhin = new ArrayList<shouhinBean>();
	int Balance = 0;
	int thousand_yen_mount = 0;
	int five_hundred_yen_mount = 0;
	int hundred_yen_mount = 0;
	int fifteen_yen_mount = 0;
	int ten_yen_mount = 0;

	InputStreamReader isr = null;
	BufferedReader br = null;
	DBConnection db = null;

	/**
	 * @author t-murase
	 * DBConnectionとBufferedReaderのインスタンスを生成
	 * いちいち生成するのもどうかと思ったのでコンストラクタで生成
	 * 特にBufferedReaderは使い終わるたびにcloseするとエラーが吐かれるので最後にまとめてcloseする
	 */
	public JihankiAction() {
		db = new DBConnection();
		shouhin = db.getshouhin();
		isr = new InputStreamReader(System.in);
		br = new BufferedReader(isr);
	}

	/**
	 * @author t-murase
	 * 利用者からの入力を待つためのメソッド
	 * 入力があったら入力に対応した処理を実行
	 */
	public void waitForAction() {
		HashMap<String, String> shouhin_name_hash = new HashMap<String, String>();
		for(shouhinBean sb:shouhin) {
			shouhin_name_hash.put(sb.getName(), sb.getName());
		}
        String order = null;
        int shouhin_count = 0;
        Tsurisen ts = new Tsurisen(br);
        if(ts.isTsurisenEmpty()) {
        	ts.chargeTsurisen();
        }
        try {
        	System.out.println(WAIT_FOR_ACTION);
        	while(true) {
        		shouhinLump();
        		ZandakaLump();
				order = br.readLine();
				if(shouhin_name_hash.get(order) != null|| winning_lottery) {//商品名を入力。入力されたらボタンが押されたものとみなす
					pushButton(order);
				}else if(order.endsWith(YEN)) {//語尾にyenとついていれば~~円入れられた物としてみなす
					addBalance(order);
				}else if(order.equals(REPAYMENT)) {//repaymentと入力すれば入れたお金が帰ってくる
					returnBalance();
				}else if(order.endsWith(E_MONEY)) {//語尾にeと付ければ電子マネーでお金を追加したものとする
					addBalanceByElectronic(order);
				}else if(order.endsWith(SAPICA_MONEY)) {//語尾にeと付ければ電子マネーでお金を追加したものとする
					addBalanceBySapica(order);
				}else if(order.contentEquals(SHUT_DOWN)) {//shutdownと入力すればシャットダウンされる
					closeing();
					return;
				}else if(order.equals(CHARGE_TSURISEN)) {//charge_tsurisenと入力すれば釣り銭補充モードになる
					ts.chargeTsurisen();
				}else if(order.equals(CHARGE_SHOUHIN)) {//charge_shouhinと入力すれば商品補充モードになる。endと入力すれば
					while(!userAuthment());
					while(true) {
						System.out.println(SHOUHIN_HOJU);
						order = br.readLine();
						if(order.equals(END_OF_HOJU)){//商品名を入力した後に補充したい量を入力する。endと入力すれば補充を終える。
							shouhin = db.getshouhin();
							break;
						}else if(shouhin_name_hash.get(order) != null) {
							System.out.println(INPUT_SHOUHIN_MOUNT);
							shouhin_count = Integer.parseInt(br.readLine());
							db.replenishShouhin(shouhin_count, order);
						}
					}
				}else if(order.equals(SHINSAI)) {//shinsaiと入力すれば震災がおきたものとみなす。震災がおきたら全ての商品が０円になる
					whenShinsai();

				}
		        if(ts.isTsurisenEmpty()) {
		        	noTsurisen();
		        	ts.chargeTsurisen();
		        }
        	}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @author t-murase
	 * @param shouhin_name
	 * 商品ボタンが押された時の処理
	 * もし商品ボタンが押される前にお金が入っていれば商品が出てくる。
	 * 入っていないと商品が選択された状態になる
	 */

	public void pushButton(String shouhin_name) {
		if(0 < getShouhinCount(shouhin_name)) {
			selected_shouhin = shouhin_name;
		}
		int selected_shouhin_price =  getShouhinPrice(shouhin_name);
		if(selected_shouhin_price <= Balance || shinsai ||winning_lottery) {
			buyShouhin();
		}
	}

	/**
	 * @author t-murase
	 * @param moneyName
	 * 現物のお金で自動販売機に残高を追加する
	 * 入れられるお金は1,000円、500円、100円、50円、10円
	 * それ以外のお金は(DBに登録されていないお金は)返却される
	 * もしお金を入れる前に商品が選択されており、選択された商品の値段以上のお金が入ったなら商品が出てくる
	 * 現物のお金を自動販売機に入れたら電子マネーは追加できなくなる
	 * 残高の最高額は7600円
	 */
	public void addBalance(String moneyName) {
		if(!can_add_balance_by_real_money) {
			return;
		}
		String money = null;
		StringBuilder sb = new StringBuilder();
		DBConnection dbcon = new DBConnection();
		List<MoneyBean> Moneies = dbcon.getMoneies();
		for(int i = 0;i < Moneies.size();i++) {
			if(Moneies.get(i).getMoneyName().equals(moneyName)) {
				sb = new StringBuilder(moneyName);
				money = sb.replace(sb.indexOf(Y), sb.indexOf(N)+1, "").toString();
				if(Balance + Integer.parseInt(money) <= MAX_ZANDAKA) {
					Balance += Integer.parseInt(money);
				}else {
					return;
				}
				System.out.println(Integer.parseInt(money));
				switch(Integer.parseInt(money)){
				case 1000:
					thousand_yen_mount += 1;
					break;
				case 500:
					five_hundred_yen_mount += 1;
					break;
				case 100:
					hundred_yen_mount += 1;
					break;
				case 50:
					fifteen_yen_mount += 1;
					break;
				case 10:
					ten_yen_mount += 1;
					break;
				}
				break;
			}
			if(i == Moneies.size() -1) {
				System.out.println(BACKED_MONEY);
			}
		}

		can_add_balance_by_card  = false;
		can_add_balance_by_sapica  = false;
		selectedShouhin();
	}

	public void selectedShouhin() {
		int selected_shouhin_price =  0;
		if(selected_shouhin != null) {
			selected_shouhin_price = getShouhinPrice(selected_shouhin);
			if(selected_shouhin_price <= Balance) {
				selected_shouhin_price =  getShouhinPrice(selected_shouhin);
				buyShouhin();
			}
		}
	}
	/**
	 * @author t-murase
	 * @param Electronic_charge
	 * 電子マネーで自動販売機に残高を追加する。
	 * 電子マネーでお金を追加したら商品を買う、またはお金を返却するまで残高を追加することはできない。
	 */
	public void addBalanceByElectronic(String Electronic_charge) {
		if(!can_add_balance_by_card) {
			return;
		}
		StringBuilder sb = new StringBuilder(Electronic_charge);
		Balance = Integer.parseInt(sb.delete(sb.indexOf(E_MONEY), sb.indexOf(E_MONEY) + 1).toString());
		can_add_balance_by_real_money = false;
		can_add_balance_by_card = false;
		can_add_balance_by_sapica  = false;
		electronic = true;
		selectedShouhin();
	}

	/**
	 * @author t-murase
	 * @param Electronic_charge
	 * Sapicaで自動販売機に残高を追加する。
	 * Sapicaでお金を追加したら商品を買う、またはお金を返却するまで残高を追加することはできない。
	 */
	public void addBalanceBySapica(String sapica_charge) {
		if(!can_add_balance_by_card) {
			return;
		}
		StringBuilder sb = new StringBuilder(sapica_charge);
		Balance = Integer.parseInt(sb.delete(sb.indexOf(SAPICA_MONEY), sb.indexOf(SAPICA_MONEY) + 1).toString());
		sapica = true;
		can_add_balance_by_real_money = false;
		can_add_balance_by_card = false;
		can_add_balance_by_sapica = false;
		selectedShouhin();
	}

	/**
	 *  @author t-murase
	 *  お金を返却する。
	 */
	public void returnBalance() {
		if(can_add_balance_by_real_money && winning_lottery == false) {
			System.out.println(THOUSAND_YEN+ thousand_yen_mount + MAI);
			System.out.println(FIVE_HUNDRED_YEN+ five_hundred_yen_mount + MAI);
			System.out.println(HUNDRED_YEN+ hundred_yen_mount + MAI);
			System.out.println(FIFTEEN_YEN+ fifteen_yen_mount + MAI);
			System.out.println(TEN_YEN + ten_yen_mount + MAI_DETA);
			thousand_yen_mount = 0;
			five_hundred_yen_mount = 0;
			hundred_yen_mount = 0;
			fifteen_yen_mount = 0;
			ten_yen_mount = 0;
		}else if(winning_lottery) {
		}else if(electronic){
			System.out.println(EMONEY_ZANDAKA + Balance);
		}else if(sapica) {
			System.out.println(SAPICA_ZANDAKA + Balance);
		}
		can_add_balance_by_card = true;
		can_add_balance_by_real_money = true;
		can_add_balance_by_sapica = true;
		Balance = 0;
	}

	/**
	 *   @author t-murase
	 *   自動販売機の中に釣り銭が十分にない時に
	 */
	public void noTsurisen(){
		System.out.println(NO_CHANGE);
	}

	/**
	 * @author t-murase
	 * 自動販売機にある商品が表示される
	 * 商品名の横に商品の値段が表示される
	 * もし商品が品切れになっているなら×が、現在の自動販売機にある残高で商品を買えるなら商品の横に丸が、商品が選択されているなら●がつく
	 */

	public void shouhinLump(){
		for(int i = 0; i < shouhin.size();i++) {
			System.out.print(shouhin.get(i).getName());
			if(!shinsai && !sapica) {
				System.out.print(" " + shouhin.get(i).getPrice() + YEN);
			}else if(sapica) {
				System.out.print(" ");
				System.out.print(shouhin.get(i).getPrice() - 10);
				System.out.print(YEN);
			}else {
				System.out.print(" " + 0 + YEN);
			}
			if(shouhin.get(i).getName().equals(selected_shouhin)) {
				System.out.print(KURO_MARU);
			}else if(shouhin.get(i).getShouhinCount() == 0) {
				System.out.print(BATSU);
			}else if(shouhin.get(i).getPrice() <= Balance) {
				System.out.print(SIRO_MARU);
			}
			System.out.println();
		}
	}

	/**
	 * @author t-murase
	 * 現在の残高が表示される
	 */
	public void ZandakaLump(){
		System.out.println(ZANDAKA + Balance + YEN_KANJI);
	}

	/**
	 * @author t-murase
	 * @param shouhinName
	 * @return 商品の値段
	 * 引数としてとった商品の値段を返す
	 */
	public int getShouhinPrice(String shouhinName) {
		int selected_shouhin_price = 0;
		for(int i = 0;i < shouhin.size();i++) {
			if(shouhin.get(i).getName().equals(shouhinName) && !shinsai) {
				if(sapica) {
					selected_shouhin_price  = shouhin.get(i).getPrice() - 10;
				}else {
					selected_shouhin_price  = shouhin.get(i).getPrice();
				}
				break;
			}else if(shinsai) {
				selected_shouhin_price  = 0;
			}
		}
		return selected_shouhin_price;
	}

	/**
	 * @author t-murase
	 * @param shouhinName
	 * @return 商品の個数
	 * 引数としてとった商品の在庫を返す
	 */
	public int getShouhinCount(String shouhinName) {
		int selected_shouhin_count = 0;
		for(int i = 0;i < shouhin.size();i++) {
			if(shouhin.get(i).getName().equals(shouhinName)) {
				selected_shouhin_count  = shouhin.get(i).getShouhinCount();
				break;
			}
		}
		return selected_shouhin_count;
	}

	/**
	 * @author t-murase
	 * 商品の購入処理
	 */
	public void buyShouhin() {
		StringBuilder sb = new StringBuilder(selected_shouhin);
		if(sb.indexOf(HOT) != -1) {
			sb.delete(sb.indexOf(HOT),sb.length());
		}
		if(sb.indexOf(COLD) != -1) {
			sb.delete(sb.indexOf(COLD),sb.length());
		}

		System.out.println(DERUOTO + sb.deleteCharAt(sb.indexOf(SPACE)) + DETEKITA );
		if(!shinsai){
			Balance = Balance - getShouhinPrice(selected_shouhin);
		}
		db.afterBuyZaiko(this);
		selected_shouhin = null;
		Tsurisen tu = new Tsurisen(br);

		if(!sapica && !electronic) {
			tu.plusTsurisen(this);
			tu.calcTsurisen(this);
			tu.minousTsurisen(this);
		}
		returnBalance();
		Lottery();
    	can_add_balance_by_card = true;
    	can_add_balance_by_real_money = true;
    	can_add_balance_by_sapica = true;
    	sapica = false;
    	electronic = false;
	}

	/**
	 * @author t-murase
	 * @return 当たりかどうかの判定
	 * 1/100の確率で当たりがでる。当たりが出たらもう一本ただでもらえる
	 */
	public boolean Lottery() {
		Random random = new Random(100);
		if(random.nextInt() == 0) {
			System.out.println(WINNING_LOTTERY);
			winning_lottery = true;
			return  true;
		}
		return false;
	}

	/**
	 * @author t-murase
	 * @return ユーザ認証がうまくいったかどうか
	 *ユーザ認証を行う
	 */
	public boolean userAuthment() {
    	String user = null;
    	String pass = null;
    	System.out.println(STOP_HANBAI);
    	System.out.println(OPEN_JIHANKI);
		try {
			System.out.println(USER);
			user = br.readLine();
			while(user == null) {
				user = br.readLine();
			}
			System.out.println(PASS);
			pass = br.readLine();
			while(pass == null) {
				pass = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

    	DBConnection dbcon = new DBConnection();
        if(!dbcon.getPass(user, pass)) {
        	userAuthment();
        }
        return true;
	}

	/**
	 * @author t-murase
	 * BufferedReaderやInputStreamReaderのクローズ処理
	 *  BufferedReaderは使うたびにcloseするとエラーが吐かれるのでシステム終了時にまとめて処理
	 */
	public void closeing() {
		try {
			br.close();
			isr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @author t-murase
	 * 震災がおきた時の処理
	 */
	public void whenShinsai() {
		shinsai = true;
	}
}
