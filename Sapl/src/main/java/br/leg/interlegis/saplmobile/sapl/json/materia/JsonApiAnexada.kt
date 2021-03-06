package br.leg.interlegis.saplmobile.sapl.json.materia

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import br.leg.interlegis.saplmobile.sapl.db.AppDataBase
import br.leg.interlegis.saplmobile.sapl.db.entities.base.Autor
import br.leg.interlegis.saplmobile.sapl.db.entities.materia.Anexada
import br.leg.interlegis.saplmobile.sapl.db.entities.materia.Autoria
import br.leg.interlegis.saplmobile.sapl.db.entities.materia.DocumentoAcessorio
import br.leg.interlegis.saplmobile.sapl.db.entities.materia.MateriaLegislativa
import br.leg.interlegis.saplmobile.sapl.json.JsonApiBaseAbstract
import br.leg.interlegis.saplmobile.sapl.support.Utils
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.jetbrains.anko.doAsync
import retrofit2.Retrofit
import kotlin.collections.ArrayList

class JsonApiAnexada(context:Context, retrofit: Retrofit?): JsonApiBaseAbstract(context, retrofit) {

    override val url = String.format("api/mobile/%s/%s/", Anexada.APP_LABEL, Anexada.TABLE_NAME)

    companion object {
        val chave = String.format("%s:%s", Anexada.APP_LABEL, Anexada.TABLE_NAME)
    }


    override fun syncList(list: Any?, deleted: IntArray?): Int {

        val daoAnexada = AppDataBase.getInstance(context).DaoAnexada()

        if (deleted != null && deleted.isNotEmpty()) {
            val apagar = daoAnexada.loadAllByIds(deleted)
            daoAnexada.delete(apagar)
        }

        if ((list as JsonArray).size() == 0)
            return 0

        val listaMaterias = JsonArray()
        val mapAnexada = Anexada.importJsonArray(list) as Map<Int, Anexada>

        try {
            daoAnexada.insertAll(ArrayList<Anexada>(mapAnexada.values))
        }
        catch (e: SQLiteConstraintException) {

            val jsonApiMateriaLegislativa = JsonApiMateriaLegislativa(context, retrofit)
            mapAnexada.values.forEach {

                try {
                    daoAnexada.insert(it)
                }
                catch (e: SQLiteConstraintException) {
                    listaMaterias.add(jsonApiMateriaLegislativa.getObject(it.materia_principal))
                }
            }
            jsonApiMateriaLegislativa.syncList(listaMaterias)
        }
        return mapAnexada.size
    }
}