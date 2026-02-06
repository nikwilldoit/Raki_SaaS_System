Tax management:

•	_id – INT
•	tax_name – VARCHAR(100)
•	type – VARCHAR(50)
•	percentage – DECIMAL(5,2)
•	exemption_flag – BOOLEAN
•	effective_date – DATE
•	description – VARCHAR(255)
•	status – ENUM('Active','Inactive')
•	created_by – INT
•	created_at – DATETIME



Order:

customization_id – INT
order_id – INT
product_id – INT
add_on_id – INT (nullable, if no add-ons selected)
add_on_name – VARCHAR(100)
add_on_price – DECIMAL(6,2)
removed_ingredient_id – INT (nullable)
removed_ingredient_name – VARCHAR(100)
special_request – VARCHAR(255)
allergy_note – VARCHAR(255)
quantity – INT
base_price – DECIMAL(6,2)
final_price – DECIMAL(6,2)
created_at – DATETIME
