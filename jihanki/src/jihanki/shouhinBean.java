package jihanki;

public class shouhinBean {
	public int shouhin_id = 0;
	public String name = null;
	public int price = 0;
	public int shouhinCount = 0;

	public int getShouhinCount() {
		return shouhinCount;
	}
	public void setShouhinCount(int shouhinCount) {
		this.shouhinCount = shouhinCount;
	}
	public int getShouhin_id() {
		return shouhin_id;
	}
	public void setShouhin_id(int shouhin_id) {
		this.shouhin_id = shouhin_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
}
