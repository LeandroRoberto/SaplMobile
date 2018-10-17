package br.leg.interlegis.saplmobile.sapl.db.daos.base

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import br.leg.interlegis.saplmobile.sapl.db.daos.DaoBase
import br.leg.interlegis.saplmobile.sapl.db.entities.base.Autor

@Dao
interface DaoAutor: DaoBase<Autor> {

    @get:Query("SELECT * FROM "+ Autor.TABLE_NAME+" order by nome")
    val all: LiveData<List<Autor>>

    @Query("SELECT * FROM "+ Autor.TABLE_NAME+" WHERE uid IN (:autorIds)")
    fun loadAllByIds(autorIds: IntArray): List<Autor>

    @Query("SELECT * FROM "+ Autor.TABLE_NAME+" WHERE uid = :autorId")
    fun getLDAutor(autorId: Int): LiveData<Autor>
}