package br.leg.interlegis.saplmobile.sapl.db.daos.sessao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import br.leg.interlegis.saplmobile.sapl.db.daos.DaoBase
import br.leg.interlegis.saplmobile.sapl.db.entities.sessao.SessaoPlenaria

@Dao
interface DaoSessaoPlenaria: DaoBase<SessaoPlenaria> {

    @get:Query("SELECT * FROM "+ SessaoPlenaria.TABLE_NAME+" order by data_inicio desc, hora_inicio desc")
    override val all: LiveData<List<SessaoPlenaria>>

    @get:Query("SELECT * FROM "+ SessaoPlenaria.TABLE_NAME+" order by data_inicio desc, hora_inicio desc")
    val all_direct: List<SessaoPlenaria>

    @Query("SELECT * FROM "+ SessaoPlenaria.TABLE_NAME+" WHERE uid IN (:sessaoIds)")
    fun loadAllByIds(sessaoIds: IntArray): List<SessaoPlenaria>

    @Query("SELECT 1 FROM "+ SessaoPlenaria.TABLE_NAME+" WHERE uid = :sessaoId")
    fun exists(sessaoId: Int): Boolean

    @Query("SELECT * FROM "+ SessaoPlenaria.TABLE_NAME+" WHERE uid = :sessaoId")
    fun getLDSessao(sessaoId: Int): LiveData<SessaoPlenaria>

}