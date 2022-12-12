package models

case class SaleRecord(
                       ws_item_sk: Int,
                       ws_order_number: Int,
                       i_brand_id: Int,
                       i_class_id: Int,
                       i_category_id: Int,
                       i_manufact_id: Int,
                       ca_city: String,
                       ca_county: String,
                       ca_state: String,
                       ca_country: String,
                       sm_type: String,
                       sm_code: String,
                       sm_carrier: String,
                       sale_at: String
                     ) extends Serializable {
}
