/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.view;

import hr.algebra.dal.Repository;
import hr.algebra.dal.RepositoryFactory;
import hr.algebra.model.Movie;
import hr.algebra.parsers.rss.MovieParser;
import hr.algebra.utils.MessageUtils;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;

/**
 *
 * @author Patrik
 */
public class AdminPanel extends javax.swing.JPanel {

    private DefaultListModel<Movie> movieModel;
    private Repository repo;
    
    /**
     * Creates new form AdminPanel
     */
    public AdminPanel() {
        initComponents();
        init();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        lsMovies = new javax.swing.JList<>();
        btnUploadMovies = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(1200, 777));

        jScrollPane1.setViewportView(lsMovies);

        btnUploadMovies.setText("Upload Movies");
        btnUploadMovies.setActionCommand("Upload articles");
        btnUploadMovies.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadMoviesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1180, Short.MAX_VALUE)
                    .addComponent(btnUploadMovies, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 664, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnUploadMovies, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnUploadMoviesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadMoviesActionPerformed
        try {
            repo.deleteMovies();
            List<Movie> movies = MovieParser.parse();
            repo.createMovies(movies);
            loadModel();
        } catch (Exception ex) {
            MessageUtils.showErrorMessage("Unrecoverable error", "Unable to upload movies!");
        }
    }//GEN-LAST:event_btnUploadMoviesActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnUploadMovies;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList<Movie> lsMovies;
    // End of variables declaration//GEN-END:variables

    private void loadModel() throws Exception {
        List<Movie> movies = repo.selectMovies();
        movieModel.clear();
        movies.forEach(movieModel::addElement);
        lsMovies.setModel(movieModel);
    }

    private void init() {
        try{
            repo = RepositoryFactory.getRepository();
            movieModel = new DefaultListModel<>();
            loadModel();
        }
        catch(Exception ex){
            Logger.getLogger(AdminPanel.class.getName()).log(Level.SEVERE, null, ex);
            MessageUtils.showErrorMessage("Unrecoverable error", "Cannot initiate the form");
            System.exit(1);
        }
    }

}
