package com.freetmp.mbg.plugin
import com.freetmp.mbg.plugin.batch.BatchInsertPlugin
import com.freetmp.mbg.plugin.batch.BatchUpdatePlugin
import groovy.util.logging.Slf4j
import org.mybatis.generator.api.dom.java.Method
import org.mybatis.generator.api.dom.xml.XmlElement
/**
 * Created by LiuPin on 2015/5/21.
 */
@Slf4j
class BatchPluginSpec extends AbstractPluginSpec {

  def "check generated client interface and mapper xml for batch update"() {
    setup:
    BatchUpdatePlugin plugin = new BatchUpdatePlugin()
    XmlElement element

    when:
    plugin.clientGenerated(mapper, mapperImpl, introspectedTable)

    then:
    1 * mapper.addMethod { Method method -> method.getFormattedContent(0, true) == "int batchUpdate(List<User> list);" }
    1 * mapper.addImportedTypes({ it.size() >= 1 })

    when:
    plugin.sqlMapDocumentGenerated(document, introspectedTable)

    then:
    1 * root.addElement({ isXmlElementWithIdEquals(it, BatchUpdatePlugin.BATCH_UPDATE) }) >> { element = it }

    when:
    println parseSql(element, [list: [
        [id: 1, loginName: "admin", name: "Admin", password: "12345678", salt: "123", roles: "admin", registerDate: new Date()] as User,
        [id: 2, loginName: "user", name: "User", password: "12345678", salt: "123", roles: "user", registerDate: new Date()] as User
    ]])
    log.info systemOutRule.log
    then:
    systemOutRule.log.trim() == "update user set login_name = ?, name = ?, password = ?, salt = ?, roles = ?, register_date = ? where id = ? ; update user set login_name = ?, name = ?, password = ?, salt = ?, roles = ?, register_date = ? where id = ?"
  }

  def "check generated client interface and mapper xml for batch insert"() {
    setup:
    BatchInsertPlugin plugin = new BatchInsertPlugin()
    XmlElement element

    when:
    plugin.clientGenerated(mapper, mapperImpl, introspectedTable)

    then:
    1 * mapper.addMethod { Method method -> method.getFormattedContent(0, true) == "int batchInsert(List<User> list);" }

    when:
    plugin.sqlMapDocumentGenerated(document, introspectedTable)

    then:
    1 * root.addElement({ isXmlElementWithIdEquals(it, BatchInsertPlugin.BATCH_INSERT) }) >> { element = it }

    when:
    println parseSql(element, [list: [
        [id: 1, loginName: "admin", name: "Admin", password: "12345678", salt: "123", roles: "admin", registerDate: new Date()] as User,
        [id: 2, loginName: "user", name: "User", password: "12345678", salt: "123", roles: "user", registerDate: new Date()] as User
    ]])
    log.info systemOutRule.log
    then:
    systemOutRule.log.trim() == "insert into user ( login_name, name, password, salt, roles, register_date ) values ( ?, ?, ?, ?, ?, ? ) , ( ?, ?, ?, ?, ?, ? )"

  }
}