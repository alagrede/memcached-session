package fr.lagrede.session.beanutils;

import javax.servlet.http.HttpSessionAttributeListener;

/**
 * 
 * Dans certains cas, le container JEE lance des �v�nements de replacement {@link HttpSessionAttributeListener#attributeReplaced(javax.servlet.http.HttpSessionBindingEvent)} 
 * sans avoir modifi� r�ellement l'objet en session.<br/>
 * Ce probl�me a �t� rep�r� notamment avec l'utilisation des beans sessions de spring.<br/>
 * <br/>
 * Pour parer ces faux �v�nements, il faut rendre le bean lui-m�me responsable de ses modifications.<br/> 
 * En impl�mentant {@link DirtyBean} on pourra modifier la valeur d'un boolean partout ou les donn�es sont effectivement modifi�es.<br/>
 * <br/>
 * <pre>
 *  if (sessionbean.isDirty()) {
 *      backupService(sessionBean);// backup bean
 *      sessionBean.clean(); // reset du boolean
 *  }
 * </pre>
 *  <br/>
 *  <i>
 *  Une autre solution aurait �t� de v�rifier l'�galit� des objets avec equals dans {@link HttpSessionAttributeListener} mais l'utilisation de {@link DirtyBean}
 *  permet plus de flexibilit� sur les modifications importantes ou non dans l'objet session 
 *  </i>
 * 
 * @author anthony.lagrede
 *
 */
public interface DirtyBean {

    /**
     * Est-ce que le bean a �t� r�ellement modifi�
     * @return true si modifi�
     */
    boolean isDirty();
    
    /**
     * Remise � z�ro du bool�en de modification
     */
    void clean();
    
}
