package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Employee;
import beans.Expenses;
import beans.Post;

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

	/**
	 * ID指定の検索を実施する。
	 *
	 * @param id 検索対象のID
	 * @return 検索できた場合は検索結果データを収めたPostインスタンス。検索に失敗した場合はnullが返る。
	 */
	public Expenses findById(int id) {
		Expenses result = null;

		Connection connection = ConnectionProvider.getConnection();
		if (connection == null) {
			return result;
		}

		try (PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_QUERY)) {
			statement.setInt(1, id);

			ResultSet rs = statement.executeQuery();

			if (rs.next()) {
				result = processRow(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionProvider.close(connection);
		}

		return result;
	}

	/**
	 * パラメータ指定の検索を実施する。
	 * 有効なパラメータ指定が1つも存在しない場合は全件検索になる。
	 *
	 * @param param 検索用のパラメータを収めたオブジェクト。
	 * @return 検索結果を収めたList。検索結果が存在しない場合は長さ0のリストが返る。
	 */
	public List<Expenses> findByParam(Param param) {
		List<Expenses> result = new ArrayList<>();

		Connection connection = ConnectionProvider.getConnection();
		if (connection == null) {
			return result;
		}

		String queryString = SELECT_ALL_QUERY + param.getWhereClause();
		try (PreparedStatement statement = connection.prepareStatement(queryString)) {
			param.setParameter(statement);

			ResultSet rs = statement.executeQuery();

			while (rs.next()) {
				result.add(processRow(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionProvider.close(connection);
		}

		return result;
	}

	/**
	 * 指定されたExpensesオブジェクトを新規にDBに登録する。
	 * 登録されたオブジェクトにはDB上のIDが上書きされる。
	 * 何らかの理由で登録に失敗した場合、IDがセットされない状態（=0）で返却される。
	 *
	 * @param Expenses 登録対象オブジェクト
	 * @return DB上のIDがセットされたオブジェクト
	 */
	public Expenses create(Expenses expenses) {
		Connection connection = ConnectionProvider.getConnection();
		if (connection == null) {
			return expenses;
		}

		try (PreparedStatement statement = connection.prepareStatement(INSERT_QUERY, new String[] { "ID" });) {
			// INSERT実行
			setParameter(statement, expenses, false);
			statement.executeUpdate();

			// INSERTできたらKEYを取得
			ResultSet rs = statement.getGeneratedKeys();
			rs.next();
			int id = rs.getInt(1);
			expenses.setId(id);
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			ConnectionProvider.close(connection);
		}

		return expenses;

		/**
		 * 指定されたExpensesオブジェクトを使ってDBを更新する。
		 *
		 * @param expenses 更新対象オブジェクト
		 * @return 更新に成功したらtrue、失敗したらfalse
		 */
		public Expenses update(Expenses expenses) {
			Connection connection = ConnectionProvider.getConnection();
			if (connection == null) {
				return expenses;
			}

			try (PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {
				setParameter(statement, expenses, true);
				statement.executeUpdate();
			} catch (SQLException ex) {
				ex.printStackTrace();
			} finally {
				ConnectionProvider.close(connection);
			}

			return expenses;
		}
// ここから先Employeeのとこからコピーしたのみ
		/**
		 * 検索結果からオブジェクトを復元する。
		 *
		 * @param rs 検索結果が収められているResultSet。rs.next()がtrueであることが前提。
		 * @return 検索結果を収めたオブジェクト
		 * @throws SQLException 検索結果取得中に何らかの問題が発生した場合に送出される。
		 */
		private Employee processRow(ResultSet rs) throws SQLException {
			Employee result = new Employee();

			// Employee本体の再現
			result.setId(rs.getInt("ID"));
			result.setEmpId(rs.getString("EMPID"));
			result.setName(rs.getString("NAME"));
			result.setAge(rs.getInt("AGE"));
			result.setGenderByInt(rs.getInt("GENDER"));
			result.setPhotoId(rs.getInt("PHOTOID"));	// Photoオブジェクトに関しては必要になるまで生成しない
			result.setZip(rs.getString("ZIP"));
			result.setPref(rs.getString("PREF"));
			result.setAddress(rs.getString("ADDRESS"));
			Date entDate = rs.getDate("ENTDATE");
			if (entDate != null) {
				result.setEnterDate(entDate.toString());
			}
			Date retDate = rs.getDate("RETDATE");
			if (retDate != null) {
				result.setRetireDate(retDate.toString());
			}

			// 入れ子のオブジェクトの再現
			Post post = new Post();
			post.setId(rs.getInt("POSTID"));
			post.setName(rs.getString("POST_NAME"));
			result.setPost(post);

			return result;
		}

		/**
		 * オブジェクトからSQLにパラメータを展開する。
		 *
		 * @param statement パラメータ展開対象のSQL
		 * @param employee パラメータに対して実際の値を供給するオブジェクト
		 * @param forUpdate 更新に使われるならtrueを、新規追加に使われるならfalseを指定する。
		 * @throws SQLException パラメータ展開時に何らかの問題が発生した場合に送出される。
		 */
		private void setParameter(PreparedStatement statement, Employee employee, boolean forUpdate) throws SQLException {
			int count = 1;
			statement.setString(count++, employee.getEmpId());
			statement.setString(count++, employee.getName());
			statement.setInt(count++, employee.getAge());
			statement.setInt(count++, employee.getGender().ordinal());
			statement.setInt(count++, employee.getPhotoId());
			statement.setString(count++, employee.getZip());
			statement.setString(count++, employee.getPref());
			statement.setString(count++, employee.getAddress());
			statement.setInt(count++, employee.getPost().getId());
			if (employee.getEnterDate() != null) {
				statement.setDate(count++, Date.valueOf(employee.getEnterDate()));
			} else {
				statement.setDate(count++, null);
			}
			if (employee.getRetireDate() != null) {
				statement.setDate(count++, Date.valueOf(employee.getRetireDate()));
			} else {
				statement.setDate(count++, null);
			}

			if (forUpdate) {
				statement.setInt(count++, employee.getId());
			}
		}

}