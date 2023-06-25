package dataAndRelation;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AulaDao {

    @Insert
    void insertAula(Aula aula);

    @Update
    void updateAula(Aula aula);

    @Delete
    void deleteAula(Aula aula);

    @Query("SELECT * FROM aula_universita ORDER BY nomeEdificio DESC")
    LiveData<List<Aula>> getAllAule();
}
