package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import beans.Expenses;

/**
 * 経費データを扱うDAO
 */
public class ExpensesDAO {
	/**
	 * クエリ文字列
	 */
	private static final String SELECT_ALL_QUERY = " SELECT EXP.ID AS ID, EXP.APPID, EXP.REPDATE, EXP.UPDATEDATE, EXP.NAME, EXP.TITLE, EXP.PAYEE, "
			+ "EXP.PRICE, EXP.STATUS, EXP.UPNAME FROM EXPENSES EXP ORDER BY EXP.ID";

	private static final String SELECT_BY_ID_QUERY = " SELECT EXP.ID AS ID, EXP.APPID, EXP.REPDATE, EXP.UPDATEDATE, EXP.NAME, EXP.TITLE, EXP.PAYEE, "
			+ "EXP.PRICE, EXP.STATUS, EXP.UPNAME FROM EXPENSES EXP " + " WHERE ID = ?";

	private static final String INSERT_QUERY = "INSERT INTO "
			+ "EXPENSES(ID, APPID, REPDATE, UPDATEDATE, NAME, TITLE, PAYEE, PRICE, STATUS, UPNAME) " + "VALUES(expenses_ID.NEXTVAL,?,?,?,?,?,?,?,?,?)";

	private static final String UPDATE_QUERY = "UPDATE EXPENSES "
			+ "SET APPID=?,REPDATE=?,UPDATEDATE=?,NAME=?,TITLE=?,PAYEE=?,PRICE=?,UPNAME=?" + "WHERE ID = ?";

	private static final String DELETE_QUERY = "DELETE FROM EXPENSES WHERE ID = ?";

	/**
	 * 経費の全件を取得する。
	 *
	 * @return DBに登録されている部署データ全件を収めたリスト。途中でエラーが発生した場合は空のリストを返す。
	 */
	public List<Expenses> findAll() {
		List<Expenses> result = new ArrayList<>();

		Connection connection = ConnectionProvider.getConnection();
		if (connection == null) {
			return result;
		}

		try (Statement statement = connection.createStatement();) {
			ResultSet rs = statement.executeQuery(SELECT_ALL_QUERY);

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
	 * ID指定の検索を実施する。
	 *
	 * @param id
	 *            検索対象のID
	 * @return 検索できた場合は検索結果データを収めたExpensesインスタンス。検索に失敗した場合はnullが返る。
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
	 * パラメータ指定の検索を実施する。 有効なパラメータ指定が1つも存在しない場合は全件検索になる。
	 *
	 * @param param
	 *            検索用のパラメータを収めたオブジェクト。
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
	 * 指定されたExpensesオブジェクトを新規にDBに登録する。 登録されたオブジェクトにはDB上のIDが上書きされる。
	 * 何らかの理由で登録に失敗した場合、IDがセットされない状態（=0）で返却される。
	 *
	 * @param Expenses
	 *            登録対象オブジェクト
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
	}

	/**
	 * 指定されたExpensesオブジェクトを使ってDBを更新する。
	 *
	 * @param expenses
	 *            更新対象オブジェクト
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

	/**
	 * 指定されたIDのExpensesデータを削除する。
	 *
	 * @param id 削除対象のExpensesデータのID
	 * @return 削除が成功したらtrue、失敗したらfalse
	 */
	public boolean remove(int id) {
		Connection connection = ConnectionProvider.getConnection();
		if (connection == null) {
			return false;
		}

		int count = 0;
		try (PreparedStatement statement = connection.prepareStatement(DELETE_QUERY)) {
			// DELETE実行
			statement.setInt(1, id);
			count = statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionProvider.close(connection);
		}
		return count == 1;
	}

	/**
	 * 検索結果からオブジェクトを復元する。
	 *
	 * @param rs
	 *            検索結果が収められているResultSet。rs.next()がtrueであることが前提。
	 * @return 検索結果を収めたオブジェクト
	 * @throws SQLException
	 *             検索結果取得中に何らかの問題が発生した場合に送出される。
	 */
	private Expenses processRow(ResultSet rs) throws SQLException {
		Expenses result = new Expenses();

		// Expenses本体の再現
		result.setId(rs.getInt("ID"));
		result.setAppId(rs.getString("APPID"));
		result.setRepDate(rs.getString("REPDATE"));
		result.setUpDate(rs.getString("UPDATEDATE"));
		result.setName(rs.getString("NAME"));
		result.setTitle(rs.getString("TITLE"));
		result.setPayee(rs.getString("PAYEE"));
		result.setPrice(rs.getInt("PRICE"));
		result.setExstatusByInt(rs.getInt("STATUS"));
		result.setUpName(rs.getString("UPNAME"));
		//result.setRejectReason(rs.getString("REJECTREASON"));
		return result;
	}

	/**
	 * オブジェクトからSQLにパラメータを展開する。
	 *
	 * @param statement
	 *            パラメータ展開対象のSQL
	 * @param employee
	 *            パラメータに対して実際の値を供給するオブジェクト
	 * @param forUpdate
	 *            更新に使われるならtrueを、新規追加に使われるならfalseを指定する。
	 * @throws SQLException
	 *             パラメータ展開時に何らかの問題が発生した場合に送出される。
	 */
	private void setParameter(PreparedStatement statement, Expenses expenses, boolean forUpdate) throws SQLException {
		int count = 1;
		statement.setString(count++, expenses.getAppId());
		statement.setString(count++, expenses.getRepDate());
		if (expenses.getUpDate() != null) {
			statement.setDate(count++, Date.valueOf(expenses.getUpDate()));
		} else {
			statement.setDate(count++, null);
		}
		statement.setString(count++, expenses.getName());
		statement.setString(count++, expenses.getTitle());
		statement.setString(count++, expenses.getPayee());
		statement.setInt(count++, expenses.getPrice());
		statement.setInt(count++, expenses.getExstatus().ordinal());
		statement.setString(count++, expenses.getUpName());
		//statement.setString(count++, expenses.getRejectReason());

		if (forUpdate) {
			statement.setInt(count++, expenses.getId());
		}
	}

}