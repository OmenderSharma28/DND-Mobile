package model;

public class BlockNumberBean {
	String name, number , type ,rowId,contactId,block_since;

public String getBlock_since() {
		return block_since;
	}

	public void setBlock_since(String block_since) {
		this.block_since = block_since;
	}

	//	public BlockNumberBean(String name, String number, String type,
//			String rowId, String contactId) {
//		super();
//		this.name = name;
//		this.number = number;
//		this.type = type;
//		this.rowId = rowId;
//		this.contactId = contactId;
//	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}



	public void setNumber(String number) {
		this.number = number;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRowId() {
		return rowId;
	}

	public void setRowId(String rowId) {
		this.rowId = rowId;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

}
