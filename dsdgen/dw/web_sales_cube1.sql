CREATE VIEW web_sales_cube1 AS
SELECT web_sales.ws_item_sk,
    web_sales.ws_order_number,
	web_sales.ws_quantity,
	web_sales.ws_ext_list_price,
    item.i_brand_id,
    item.i_class_id,
    item.i_category_id,
    item.i_manufact_id,
    customer_address.ca_city,
    customer_address.ca_county,
    customer_address.ca_state,
    customer_address.ca_country,
    trim(TRAILING FROM ship_mode.sm_type) as sm_type,
    trim(TRAILING FROM ship_mode.sm_code) as sm_code,
    trim(TRAILING FROM ship_mode.sm_carrier) as sm_carrier,
    to_timestamp(CONCAT(to_char(date_dim.d_date, 'YYYY-MM-DD'), ' ', time_dim.t_hour, ':', time_dim.t_minute, ':', time_dim.t_second), 'YYYY-MM-DD HH24:MI:SS') as sale_at
FROM web_sales, date_dim, time_dim, item, customer_address, ship_mode
WHERE web_sales.ws_sold_date_sk = date_dim.d_date_sk
AND web_sales.ws_sold_time_sk = time_dim.t_time_sk
AND web_sales.ws_item_sk = item.i_item_sk
AND web_sales.ws_ship_addr_sk = customer_address.ca_address_sk
AND web_sales.ws_ship_mode_sk = ship_mode.sm_ship_mode_sk

-- https://www.postgresqltutorial.com/postgresql-date-functions/postgresql-date_trunc/
select sum(ws_ext_list_price) as sum_ws_ext_list_price, DATE_TRUNC('hour', sale_at), count(*) as number_of_sales
from web_sales_cube1
GROUP BY DATE_TRUNC('hour', sale_at)