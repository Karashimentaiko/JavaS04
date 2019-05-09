package java_s04;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import beans.Expenses;
import beans.Exstatus;
import dao.ExpensesDAO;

/**
 * 経費関連のサービス実装。
 */
@Path("expenses")
public class ExpensesResource {
	private final ExpensesDAO expDao = new ExpensesDAO();

	/**
	 * 一覧用に部署情報を全件取得する。
	 * @return 部署情報のリストをJSON形式で返す。
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Expenses> findAll() {
		return expDao.findAll();
	}

	/**
	 * ID指定で経費情報を取得する。
	 *
	 * @param id
	 *            取得対象の経費のID
	 * @return 取得した経費情報をJSON形式で返す。データが存在しない場合は空のオブジェクトが返る。
	 */
	@GET
	@Path("{id}") // 自分の関数のパス、数字、その都度変えられるようになっている
	@Produces(MediaType.APPLICATION_JSON)
	public Expenses findById(@PathParam("id") int id) {
		return expDao.findById(id);
	}

	/**
	 * 指定した経費情報を登録する。
	 *
	 * @param form
	 *            経費情報を収めたオブジェクト
	 * @return DB上のIDが振られた経費情報
	 * @throws WebApplicationException
	 *             入力データチェックに失敗した場合に送出される。
	 */
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Expenses create(final FormDataMultiPart form) throws WebApplicationException {
		Expenses expenses = new Expenses();

		expenses.setId(0);
		expenses.setAppId(form.getField("appId").getValue());
		expenses.setRepDate(form.getField("repDate").getValue());
		expenses.setUpDate(form.getField("upDate").getValue());
		expenses.setName(form.getField("name").getValue());
		expenses.setTitle(form.getField("title").getValue());
		expenses.setPayee(form.getField("payee").getValue());
		expenses.setPrice(Integer.parseInt(form.getField("price").getValue()));
		String exstatus = form.getField("exstatus").getValue();
		expenses.setExstatus(Exstatus.valueOf(exstatus));

		if (!expenses.isValidObject()) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}

		return expDao.create(expenses);
	}

	/**
	 * 指定した情報でDBを更新する。
	 *
	 * @param form
	 *            更新情報を含めた経費情報
	 * @throws WebApplicationException
	 *             入力データチェックに失敗した場合に送出される。
	 */
	@PUT
	@Path("{id}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Expenses update(@PathParam("id") int id, final FormDataMultiPart form) throws WebApplicationException {
		Expenses expenses = new Expenses();

		expenses.setId(id);
		expenses.setAppId(form.getField("appId").getValue());
		expenses.setRepDate(form.getField("repDate").getValue());
		expenses.setUpDate(form.getField("upDate").getValue());
		expenses.setName(form.getField("name").getValue());
		expenses.setTitle(form.getField("title").getValue());
		expenses.setPayee(form.getField("payee").getValue());
		expenses.setPrice(Integer.parseInt(form.getField("price").getValue()));
		String exstatus = form.getField("exstatus").getValue();
		expenses.setExstatus(Exstatus.valueOf(exstatus));

		if (!expenses.isValidObject()) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}

		return expDao.update(expenses);
	}

	/**
	 * 指定したIDの経費情報を削除する。
	 *
	 * @param id 削除対象の経費情報のID
	 */
	@DELETE
	@Path("{id}")
	public void remove(@PathParam("id") int id) {
		expDao.remove(id);
	}
}
