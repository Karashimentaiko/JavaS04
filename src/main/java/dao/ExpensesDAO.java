package dao;

/**
 * 社員データを扱うDAO
 */
public class ExpensesDAO {
	/**
	 * クエリ文字列
	 */
	private static final String SELECT_ALL_QUERY =
			// "SELECT EMP.ID AS ID, EMP.EMPID, EMP.NAME, EMP.AGE, EMP.GENDER,
			// EMP.PHOTOID, EMP.ZIP, EMP.PREF, EMP.ADDRESS, "
			// + "EMP.POSTID, POST.NAME as POST_NAME, EMP.ENTDATE, EMP.RETDATE "
			// + "FROM EMPLOYEE EMP "
			// + "INNER JOIN POST POST " + "ON EMP.POSTID = POST.ID";
			"SELECT \n" + "EXP.ID \n" + ",EXP.APPID \n" + ",EXP.REPDATE \n" + ",EXP.UPDATEDATE \n" + ",EXP.NAME \n"
					+ ",EXP.TITLE \n" + ",EXP.PAYEE \n" + ",EXP.PRICE \n" + ",EXP.STATUS \n" + ",EXP.UPNAME \n" + "FROM \n"
					+ "EXPENSES EXP\n" + "ORDER BY \n" + "EXP.ID; \n";
	// 上記は却下理由は入っていない
	private static final String SELECT_BY_ID_QUERY = SELECT_ALL_QUERY + " WHERE EMP.APPID = ?";
	private static final String INSERT_QUERY = "INSERT INTO "
			+ "EXPENSES(APPID, REPDATE, UPDATEDATE, NAME, TITLE, PAYEE, PRICE, UPNAME) "
			+ "VALUES(?,?,?,?,?,?,?,?)";
	private static final String UPDATE_QUERY = "UPDATE EXPENSES "
			+ "SET APPID=?,REPDATE=?,UPDATEDATE=?,NAME=?,TITLE=?,PAYEE=?,PRICE=?,UPNAME=?"
			+ "WHERE ID = ?";
	private static final String DELETE_QUERY = "DELETE FROM EXPENSES WHERE ID = ?";
}