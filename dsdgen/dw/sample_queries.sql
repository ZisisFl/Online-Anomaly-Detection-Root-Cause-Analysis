-- Group data in 10 minute (600 seconds) chunks and sum metric
SELECT SUM(ws_quantity) as current,
to_timestamp(floor((extract('epoch' from sale_at) / 600 )) * 600) AT TIME ZONE 'UTC' as interval_alias
FROM web_sales_cube_nonull
GROUP BY interval_alias

-- group data into slides of 5 minutes
SELECT MIN(current), MAX(current), AVG(current) FROM (
SELECT max(current) as current, window_starting_timestamp
FROM (
SELECT
	ws_quantity,
    sale_at,
    SUM(ws_quantity) OVER (ORDER BY sale_at RANGE BETWEEN interval '5 minutes' PRECEDING AND CURRENT ROW) as current,
	FIRST_VALUE(sale_at) OVER (ORDER BY sale_at RANGE BETWEEN interval '5 minutes' PRECEDING AND CURRENT ROW) as window_starting_timestamp
FROM web_sales_cube_nonull
	) T1
GROUP BY window_starting_timestamp
ORDER BY window_starting_timestamp
	)T2

-- sales with data and time
select store_sales.*,
    date_dim.d_date,
    time_dim.t_hour, 
    time_dim.t_minute, 
    time_dim.t_second, 
    time_dim.t_am_pm
from store_sales, date_dim, time_dim
where store_sales.ss_sold_date_sk = date_dim.d_date_sk
and store_sales.ss_sold_time_sk = time_dim.t_time_sk
limit 1000

-- min max date of sales -- scale 1 -> 1998-01-02-2003-01-02
select min(date_dim.d_date), max(date_dim.d_date)
from store_sales, date_dim
where store_sales.ss_sold_date_sk = date_dim.d_date_sk

-- number of distinct dates and times related to sales -> 1823 45647
select count(distinct(ss_sold_date_sk)) as distinct_dates, 
count(distinct(ss_sold_time_sk)) as distinct_times
from store_sales

-- number of sales per day
select date_dim.d_date as sale_date, count(*) as number_of_sales
from store_sales, date_dim
where store_sales.ss_sold_date_sk = date_dim.d_date_sk
group by date_dim.d_date

-- min and max number of sales per day -> 560 4074 (70 1123 for web sales)
select min(number_of_sales), max(number_of_sales)
from (
	select date_dim.d_date as sale_date, count(*) as number_of_sales
	from store_sales, date_dim
	where store_sales.ss_sold_date_sk = date_dim.d_date_sk
	group by date_dim.d_date
) t

-- number of sales per time
select store_sales.ss_sold_time_sk, count(*) as number_of_sales
from store_sales
group by store_sales.ss_sold_time_sk

-- min and max number of sales per time -> 4 233
select min(number_of_sales), max(number_of_sales)
from (
	select store_sales.ss_sold_time_sk, count(*) as number_of_sales
	from store_sales
	where store_sales.ss_sold_time_sk is not null
	group by store_sales.ss_sold_time_sk
) t

-- number of sales with and without time information -> 2880404 2750767
select 'all_sales' description, count(*) as number_of_sales
from store_sales
union
select 'sale_with_time_field' description, count(*) as number_of_sales
from store_sales
where ss_sold_time_sk is not null

-- sales with casted timestamp https://www.postgresql.org/docs/8.2/functions-formatting.html
select store_sales.*,
	to_timestamp(CONCAT(to_char(date_dim.d_date, 'YYYY-MM-DD'), ' ', time_dim.t_hour, ':', time_dim.t_minute, ':', time_dim.t_second), 'YYYY-MM-DD HH24:MI:SS') as sale_at
from store_sales, date_dim, time_dim
where store_sales.ss_sold_date_sk = date_dim.d_date_sk
and store_sales.ss_sold_time_sk = time_dim.t_time_sk
limit 1000

-- query for websales with some dimension
select web_sales.ws_item_sk,
web_sales.ws_order_number,
item.i_brand_id,
item.i_class_id,
item.i_category_id,
item.i_manufact_id,
customer_address.ca_city,
customer_address.ca_county,
customer_address.ca_state,
customer_address.ca_country,
ship_mode.sm_type,
ship_mode.sm_code,
ship_mode.sm_carrier,
to_timestamp(CONCAT(to_char(date_dim.d_date, 'YYYY-MM-DD'), ' ', time_dim.t_hour, ':', time_dim.t_minute, ':', time_dim.t_second), 'YYYY-MM-DD HH24:MI:SS') as sale_at
from web_sales, date_dim, time_dim, item, customer_address, ship_mode
where web_sales.ws_sold_date_sk = date_dim.d_date_sk
and web_sales.ws_sold_time_sk = time_dim.t_time_sk
and web_sales.ws_item_sk = item.i_item_sk
and web_sales.ws_ship_addr_sk = customer_address.ca_address_sk
and web_sales.ws_ship_mode_sk = ship_mode.sm_ship_mode_sk
limit 1000

select sum(ws_quantity), date_trunc('hour', sale_at)
from web_sales_cube1
group by date_trunc('hour', sale_at)


select sum(ws_quantity), date_trunc('day', sale_at), ca_state, ca_country, ca_county
from web_sales_cube1
group by date_trunc('day', sale_at), ca_state, ca_country, ca_county