package br.leg.interlegis.saplmobile.sapl.json.base

import android.content.Context
import br.leg.interlegis.saplmobile.sapl.db.AppDataBase
import br.leg.interlegis.saplmobile.sapl.db.Converters
import br.leg.interlegis.saplmobile.sapl.db.entities.base.Autor
import br.leg.interlegis.saplmobile.sapl.db.entities.sessao.SessaoPlenaria
import br.leg.interlegis.saplmobile.sapl.json.JsonApiBaseAbstract
import br.leg.interlegis.saplmobile.sapl.json.SaplApiRestResponse
import br.leg.interlegis.saplmobile.sapl.json.interfaces.SaplRetrofitService
import br.leg.interlegis.saplmobile.sapl.support.Utils
import com.google.gson.JsonObject
import org.jetbrains.anko.doAsync
import retrofit2.Retrofit
import kotlin.collections.ArrayList

class JsonApiAutorParlamentar(context:Context, retrofit: Retrofit): JsonApiBaseAbstract(context, retrofit) {

    override val url = String.format("api/mobile/%s/%s/parlamentar/", Autor.APP_LABEL, Autor.TABLE_NAME)

    companion object {
        val chave = "parlamentares:parlamentar"
    }


    override fun sync(kwargs:Map<String, Any>): Int {
        val result = super.get(kwargs)

        val listAutor = ArrayList<Autor>()

        (result["list"] as ArrayList<JsonObject>).forEach {
            listAutor.add(Autor.importJsonObject(it))
        }

        val dao = AppDataBase.getInstance(context).DaoAutor()
        val apagar = dao.loadAllByIds(result["deleted"] as IntArray)
        dao.insertAll(listAutor)
        dao.delete(apagar)

        doAsync {
            listAutor.forEach {
                if (it.fotografia.isNotEmpty())
                    Utils.DownloadAndWriteFiles.run(context, servico, it.fotografia, it.file_date_updated)
            }
        }

        return listAutor.size
    }
}