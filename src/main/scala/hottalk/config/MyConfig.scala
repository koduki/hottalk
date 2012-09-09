// vim: set ts=4 sw=4 et:
package cn.orz.pascal.hottalk.config

trait MyConfig {
  case class AmazonWebServiceConfig(accessKeyId: String, secretKey: String, associateTag: String)
  val amazon: AmazonWebServiceConfig
  case class RakutenWebServiceConfig(developerId: String)
  val rakuten: RakutenWebServiceConfig
}
