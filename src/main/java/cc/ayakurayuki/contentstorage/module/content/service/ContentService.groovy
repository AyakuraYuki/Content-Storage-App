package cc.ayakurayuki.contentstorage.module.content.service

import cc.ayakurayuki.contentstorage.common.util.EncodeUtils
import cc.ayakurayuki.contentstorage.common.util.IDUtils
import cc.ayakurayuki.contentstorage.module.content.dao.ContentDao
import cc.ayakurayuki.contentstorage.module.content.entity.Content
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by Ayakura Yuki on 2017/9/30.
 */
@Service("ContentService")
@Transactional(readOnly = true)
class ContentService {

    @Autowired
    ContentDao contentDao

    /**
     * 获取全部信息
     * @return 全体信息列表
     */
    List<Content> codexList() {
        def list = contentDao.codexList()
        for (def item : list) {
            item.json_data = EncodeUtils.decodeBase64String(item.json_data)
        }
        return list
    }

    /**
     * 查询信息
     * @param content 查询条件封装对象
     * @return 查询结果
     */
    List<Content> search(Content content) {
        def list = contentDao.search(content)
        for (def item : list) {
            item.json_data = EncodeUtils.decodeBase64String(item.json_data)
        }
        return list
    }

    /**
     * 获取对应ID的信息
     * @param id ID
     * @return 结果对象
     */
    Content get(String id) {
        def content = contentDao.get(id)
        content.json_data = EncodeUtils.decodeBase64String(content.json_data)
        return content
    }

    /**
     * 插入新信息
     * @param item
     * @param json_data
     * @return
     */
    int insert(String item, String json_data) {
        def content = new Content()
        content.id = IDUtils.UUID()
        content.item = item
        content.json_data = EncodeUtils.encodeBase64(json_data)
        contentDao.insert(content)
    }

    /**
     * 更新信息
     * @param id
     * @param item
     * @param json_data
     * @return
     */
    int update(String id, String item, String json_data) {
        def content = new Content()
        content.id = id
        content.item = item
        content.json_data = EncodeUtils.encodeBase64(json_data)
        contentDao.update(content)
    }

    /**
     * 删除信息
     * @param id
     * @return
     */
    int delete(String id) {
        contentDao.delete(get(id))
    }

}
