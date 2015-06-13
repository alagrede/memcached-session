package fr.lagrede.memcached.benchmark;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import fr.lagrede.session.client.MemcachedClientController;

public class Benchmark {
    
    private String data = "";
    
    
    public static void main( String[] args ) {

        if (args.length != 2) {
            System.out.println("2 parameters required: <fileUrl> and <loop number>");
            System.out.println("ex: java -jar memcached-benchmark.jar /ramfs/session.txt 10000");
            System.exit(0);
        }
        
        String fileUrl = args[0];
        long counter = Integer.valueOf(args[1]);
        
        // chargement de Spring        
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("benchmark-context.xml");
        applicationContext.start();
        
        
        Benchmark benchmark = new Benchmark();
        
        try {
        
            // chargement du fichier representant la session � sauvegarder
            benchmark.loadFile(fileUrl); 
            
            
            benchmark.run(counter, applicationContext);
            
            
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.exit(0);
        
    }

    private void run(long counter, ApplicationContext context) throws IOException, InterruptedException {
        
        MemcachedClientController memcachedClientController = context.getBean(MemcachedClientController.class);
        
        // intialisation du client memcached
        memcachedClientController.init();
        
        
        for (int i = 0; i < counter; i++) {
            memcachedClientController.set(createKey(), data);
            Thread.sleep(5);
        }

    }
    

    /**
     * Cr�e un UUID cl�
     * @return
     */
    private String createKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * 
     * @param fileUrl url of file
     * @return data contains in file
     * @throws FileNotFoundException
     */
    private void loadFile(String fileUrl) throws FileNotFoundException {
        
        String chaine = "";
        
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileUrl)));
            String ligne;
            while ((ligne=br.readLine())!=null){
                chaine += ligne+"\n";
            }
            br.close(); 
        }       
        catch (Exception e){
            System.out.println(e.toString());
        }
        
        data = chaine;
    }
}
