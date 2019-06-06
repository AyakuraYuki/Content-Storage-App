package cc.ayakurayuki.csa.starter.api

import cc.ayakurayuki.csa.starter.core.annotation.Token
import cc.ayakurayuki.csa.starter.core.config.Constants
import cc.ayakurayuki.csa.starter.core.entity.JsonResponse
import cc.ayakurayuki.csa.starter.core.entity.Setting
import cc.ayakurayuki.csa.starter.core.util.IDUtils
import cc.ayakurayuki.csa.starter.service.SettingService
import cc.ayakurayuki.csa.starter.util.DESUtils
import io.vertx.core.Future
import org.apache.commons.lang3.StringUtils

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * @author ayakurayuki* @date 2019/05/20-16:46
 */
@Path(value = '/api')
class ApiRest extends BaseRest {

  private SettingService settingService

  ApiRest() {
    settingService = Constants.injector.getInstance SettingService.class
  }

  @GET
  @Path(value = '/ping')
  @Produces(MediaType.APPLICATION_JSON)
  Future<JsonResponse> ping() {
    def data = Future.future { f ->
      def result = [
          't': System.currentTimeMillis()
      ]
      f.complete result
    }
    response 0, 'pong', data
  }

  @GET
  @Path(value = '/token')
  @Produces(MediaType.APPLICATION_JSON)
  Future<JsonResponse> token() {
    def resultFuture = settingService[Constants.TOKEN]
        .compose { ar ->
          if (ar == null || StringUtils.isEmpty(ar.value)) {
            def setting = [
                'id'   : IDUtils.UUID(),
                'key'  : Constants.TOKEN,
                'value': IDUtils.UUID()
            ] as Setting
            return settingService.save(setting).compose { Void -> settingService[Constants.TOKEN] }
          } else {
            return Future.succeededFuture(ar)
          }
        }
        .compose { ar ->
          def result = [
              'token': ar.value
          ]
          Future.<LinkedHashMap<String, Object>> succeededFuture result
        }
    response resultFuture
  }

  @POST
  @Path(value = '/secret')
  @Produces(MediaType.APPLICATION_JSON)
  @Token
  Future<JsonResponse> secret(@FormParam('token') @DefaultValue('') String token) {
    def resultFuture = settingService.secretKey
        .compose({ ar ->
          def result = [
              'secret': ar
          ]
          Future.<LinkedHashMap<String, Object>> succeededFuture result
        })
    response resultFuture
  }

  @GET
  @Path(value = '/des')
  @Produces(MediaType.APPLICATION_JSON)
  Future<JsonResponse> des() {
    def result = [] as LinkedHashMap<String, Object>
    def data = DESUtils.encryptFuture('233333')
        .compose { ar ->
          result['encrypted'] = ar
          DESUtils.decryptFuture(ar)
        }
        .compose { ar ->
          result['decrypted'] = ar
          Future.<LinkedHashMap<String, Object>> succeededFuture result
        }
    response data
  }

}
