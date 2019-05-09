package beans;

public class Expenses {
	/**
	 * データベースの文字数制限
	 */
	public static final int MAX_APPID_LENGTH = 100;
	public static final int MAX_NAME_LENGTH = 40;
	public static final int MAX_TITLE_LENGTH = 100;
	public static final int MAX_PAYEE_LENGTH = 100;
	public static final int MAX_UPNAME_LENGTH = 40;
	public static final int MAX_REJECTREASON_LENGTH = 100;

	/**
	 * 保持データ
	 */
	private int id;
	private String appId;
	private String repDate;
	private String upDate;
	private String name;
	private String title;
	private String payee;
	private int price;
	private Exstatus exstatus;
	private String upName;
	private String rejectReason;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getRepDate() {
		return repDate;
	}

	public void setRepDate(String repDate) {
		this.repDate = repDate;
	}

	public String getUpDate() {
		return upDate;
	}

	public void setUpDate(String upDate) {
		this.upDate = upDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPayee() {
		return payee;
	}

	public void setPayee(String payee) {
		this.payee = payee;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public Exstatus getExstatus() {
		return exstatus;
	}

	public void setExstatus(Exstatus exstatus) {
		this.exstatus = exstatus;
	}

	public void setExstatusByInt(int exstatusAsInt) {
		if (exstatusAsInt == 0) {
			this.exstatus = Exstatus.APPLYING;
		} else if (exstatusAsInt == 1) {
			this.exstatus = Exstatus.REJECT;
		} else if (exstatusAsInt == 2) {
			this.exstatus = Exstatus.ACCEPTED;
		}
	}

	public String getUpName() {
		return upName;
	}

	public void setUpName(String upName) {
		this.upName = upName;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	/**
	 * オブジェクトのデータが有効かどうか調べます。 データベースにおいて経費テーブルを登録時、 文字数制限をしていてかつ、入力必須項目であれば||
	 * 文字数制限はしているが、入力必須項目でなければ&&
	 *
	 * @return 有効な場合は true を返す
	 */
	public boolean isValidObject() {
		if ((appId == null) || (appId.getBytes().length > MAX_APPID_LENGTH)) {
			System.err.println("Expenses: Bad application id length.");
			return false;
		}
		if ((name == null) || (name.getBytes().length > MAX_NAME_LENGTH)) {
			System.err.println("Expenses: Bad name length.");
			return false;
		}
		if ((title == null) || (title.getBytes().length > MAX_TITLE_LENGTH)) {
			System.err.println("Expenses: Bad title length.");
			return false;
		}
		if ((payee == null) || (payee.getBytes().length > MAX_PAYEE_LENGTH)) {
			System.err.println("Expenses: Bad payee length.");
			return false;
		}
		if ((exstatus != Exstatus.APPLYING) && (exstatus != Exstatus.REJECT) && (exstatus != Exstatus.ACCEPTED)) {
			System.err.println("Expenses: Bad status.");
			return false;
		}
		if ((upName != null) && (upName.getBytes().length > MAX_UPNAME_LENGTH)) {
			System.err.println("Expenses: Bad update name length.");
			return false;
		}
		if ((rejectReason != null) && (rejectReason.getBytes().length > MAX_REJECTREASON_LENGTH)) {
			System.err.println("Expenses: Bad reject reason length.");
			return false;
		}
		return true;
	}
}
