/**==============================================================
   Project: MOVIE RECOMMENDATION SYSTEM
   Authors: Apoorva Tyagi, Anthony Radianto, Eryanto
   Expert System: C Language Integrated Production System (CLIPS)
   Movie Database Source: IMDb
===============================================================**/

import javax.swing.*; 
import javax.swing.border.*; 
import javax.swing.table.*;
import java.awt.*; 
import java.awt.event.*; 

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


import javax.imageio.ImageIO;

 
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

import java.io.*;
import java.util.*;

import java.net.URL;


import CLIPSJNI.*;



class MovieDemo implements ActionListener
  {  
   JFrame jfrm;
   
   DefaultTableModel movieList;
  
   JComboBox preferredYear; 
   JComboBox preferredActor; 
   JComboBox preferredGenre; 
   JComboBox preferredGenreTh; 
   JComboBox preferredDirector;
   JComboBox preferredMpaa;
   JComboBox preferredLocation; 
   
   JRadioButton theatreRadio;
   JRadioButton dvdRadio ;
   
   JButton randomButton;
   JTextArea moreDetails;
   JLabel imgLabel;
   
   
  JPanel preferencesPanelTh = new JPanel();        
  JPanel preferencesPanel = new JPanel(); 
  
  String selectedMovie;
  
  HashMap<String, String> movieMap;
  



   
   JLabel jlab; 

   String preferredActorNames[] = new String[215]; //needs to be updated 
   String preferredYearNames[] = new String[46]; 
   String preferredGenreNames[] = new String[8]; 
   String preferredLocationNames[] = {"-- Select Location --", "EAST", "WEST", "CENTRAL", "NORTH"};
   String preferredDirectorNames[] = new String[77]; //needs to be updated
   String preferredMpaaNames[]= {"-- Select MPAA-Rating --", "G", "PG", "PG-13", "R", "TV-14"};
    
   String preferredActorChoices[] = new String[215]; //needs to be updated
   String preferredYearChoices[] = new String[46]; 
   String preferredGenreChoices[] = new String[8]; 
   String preferredLocationChoices[] = {"-- Select Location --", "EAST", "WEST", "CENTRAL", "NORTH"};
   String preferredDirectorChoices[] = new String[77];//needs to be updated
   String preferredMpaaChoices[]= {"-- Select MPAA-Rating --", "G", "PG", "PG-13", "R", "TV-14"};
   

   Environment clips;
   
   boolean isExecuting = false;
   Thread executionThread;
   
   int totalCriteriaSelected = 0;
   int isRandom=0, isDVD=1, isTheatre=0;


		
   class WeightCellRenderer extends JProgressBar implements TableCellRenderer 
     {
      public WeightCellRenderer() 
        {

         super(JProgressBar.HORIZONTAL,0,3);
         setStringPainted(false);
        }
  
      public Component getTableCellRendererComponent(
        JTable table, 
        Object value,
        boolean isSelected, 
        boolean hasFocus, 
        int row, 
        int column) 
        { 
         setValue(((Number) value).intValue());
         return WeightCellRenderer.this; 
        }
     }
      
      
    WeightCellRenderer renderer ; 

   /************/
   /* MovieDemo */
   /************/
   MovieDemo()
     {  
     
     try {
        UIManager.setLookAndFeel(
            UIManager.getCrossPlatformLookAndFeelClassName());
    } catch (Exception e) { }
     	try	
     	{
     	preferredActorChoices = buildStringArray("list_actors.txt");
     	preferredYearChoices = buildStringArray("list_years.txt");
     	preferredGenreChoices = buildStringArray("list_genre.txt");
     	preferredDirectorChoices = buildStringArray("list_director.txt");
		}catch(Exception e)
    	{
    	System.out.println("Error building array:"+e.toString());
		}
      
     renderer = this.new WeightCellRenderer();

      /*===================================*/
      /* Create a new JFrame container and */
      /* assign a layout manager to it.    */
      /*===================================*/
     
      jfrm = new JFrame("Movie Recommendation System");          
      jfrm.getContentPane().setLayout(new GridLayout(1,3));
      
      
      /*=================================*/
      /* Give the frame an initial size. */
      /*=================================*/
     
      jfrm.setSize(1280,450);  
     // jfrm.setResizable(false); 
      
  
      /*=============================================================*/
      /* Terminate the program when the user closes the application. */
      /*=============================================================*/
     

      jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
    
 
      /*===============================*/
      /* Create the preferences panel. */
      /*===============================*/
      
       /*===============================*/
      /* DVD MODE FIRST			    */
      /*===============================*/
      
      GridLayout theLayout = new GridLayout(6,2);
      theLayout.setVgap(0);
      preferencesPanel.setLayout(theLayout);   
      	preferencesPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "DVD Mode - Select one or more filters", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, new Font("Cambria", Font.ITALIC, 16)));

      Font myFont = new Font("Cambria", Font.BOLD, 16);
      
      dvdRadio = new JRadioButton("Watch a DVD");
      dvdRadio.setSelected(true);
      dvdRadio.setActionCommand("DVDMode");
      dvdRadio.addActionListener(this);
      
      JLabel genreLabel = new JLabel("Genre");
      //genreLabel.setForeground(Color.red);
      genreLabel.setFont(myFont);
      preferencesPanel.add(genreLabel);
      preferredGenre = new JComboBox(preferredGenreChoices); 
      preferencesPanel.add(preferredGenre);
      preferredGenre.addActionListener(this);

	  JLabel yearLabel = new JLabel("Year");
	  yearLabel.setFont(myFont);
	  //yearLabel.setForeground(Color.red);
	  preferencesPanel.add(yearLabel);
      preferredYear = new JComboBox(preferredYearChoices); 
      preferencesPanel.add(preferredYear);
      preferredYear.addActionListener(this);

	  JLabel actorLabel = new JLabel("Actor");
	  actorLabel.setFont(myFont);
	  //actorLabel.setForeground(Color.red);
      preferencesPanel.add(actorLabel);
      preferredActor = new JComboBox(preferredActorChoices); 
      preferencesPanel.add(preferredActor);
      preferredActor.addActionListener(this);
      
      
      JLabel directorLabel = new JLabel("Director");
	  directorLabel.setFont(myFont);
	  //actorLabel.setForeground(Color.red);
      preferencesPanel.add(directorLabel);
      preferredDirector = new JComboBox(preferredDirectorChoices); 
      preferencesPanel.add(preferredDirector);
      preferredDirector.addActionListener(this);
      
      JLabel mpaaLabel = new JLabel("MPAA-Rating");
	  mpaaLabel.setFont(myFont);
	  //actorLabel.setForeground(Color.red);
      preferencesPanel.add(mpaaLabel);
      preferredMpaa = new JComboBox(preferredMpaaChoices); 
      preferencesPanel.add(preferredMpaa);
      preferredMpaa.addActionListener(this);
      
      
      /*===============================*/
      /* THEATRE MODE NEXT			    */
      /*===============================*/
      
      GridLayout theLayoutTh = new GridLayout(2,2);
      theLayout.setVgap(0);
      preferencesPanelTh.setLayout(theLayoutTh);  
      
      theatreRadio = new JRadioButton("Watch in a Theatre");
      theatreRadio.setActionCommand("TheatreMode");
      theatreRadio.addActionListener(this);

            	preferencesPanelTh.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Theatre Mode - Select one or more filters", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, new Font("Cambria", Font.ITALIC, 16)));

      
      JLabel locationLabel = new JLabel("Location");
	  locationLabel.setFont(myFont);
	  //actorLabel.setForeground(Color.red);
     preferencesPanelTh.add(locationLabel);
      preferredLocation = new JComboBox(preferredLocationChoices); 
     preferencesPanelTh.add(preferredLocation);
     preferredLocation.addActionListener(this);
      
      JLabel genreLabelTh = new JLabel("Genre");
      //genreLabel.setForeground(Color.red);
      genreLabelTh.setFont(myFont);
      preferencesPanelTh.add(genreLabelTh);
      preferredGenreTh = new JComboBox(preferredGenreChoices); 
      preferencesPanelTh.add(preferredGenreTh);
      preferredGenreTh.addActionListener(this);
      
      randomButton = new JButton("Random Movie!");
      randomButton.setFont(myFont);
      randomButton.addActionListener(this);
      
      JPanel randomPanel = new JPanel();
      randomPanel.setLayout(new FlowLayout());
      randomPanel.add(randomButton);
      
      /*==============================================*/
      /* Create a panel including the preferences and
      	 add it to the content pane.  */
      /*==============================================*/
      
     

      JPanel choicesPanel = new JPanel(); 
      choicesPanel.setLayout(new BoxLayout(choicesPanel, BoxLayout.Y_AXIS));
      choicesPanel.add(dvdRadio);
      choicesPanel.add(preferencesPanel);
      choicesPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
      choicesPanel.add(theatreRadio);
      choicesPanel.add(preferencesPanelTh); 
      choicesPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
      choicesPanel.add(randomPanel);
      
       ButtonGroup groupRadio = new ButtonGroup();
       groupRadio.add(dvdRadio);
       groupRadio.add(theatreRadio);
       
       
     // jfrm.getContentPane().add(choicesPanel, BorderLayout.WEST); 
      jfrm.getContentPane().add(choicesPanel); 
      
      enableComponents(preferencesPanel, true);
      enableComponents(preferencesPanelTh, false);
      
 
      /*==================================*/
      /* Create the recommendation panel. */
      /*==================================*/

      movieList = new DefaultTableModel();

     movieList.setDataVector(new Object[][] { }, 
     						 new Object[] { "Movie Title", "Strength of Recommendation"});
         
      final JTable table = 
         new JTable(movieList)
           {
            public boolean isCellEditable(int rowIndex,int vColIndex) 
              { return false; }
           };

	table.setFont(new Font("Calibri", Font.ITALIC, 16));


      table.setCellSelectionEnabled(true); 

      //renderer.setBackground(table.getBackground());
      renderer.setForeground(Color.green);

      table.getColumnModel().getColumn(1).setCellRenderer(renderer);

      JScrollPane pane = new JScrollPane(table);
      
    //  JPanel listPanel = new JPanel();
    
 //     table.setPreferredScrollableViewportSize(new Dimension(250,190)); 
        
      /*===================================================*/
      /* Add the recommendation panel to the content pane. */
      /*===================================================*/

    //  jfrm.getContentPane().add(pane, BorderLayout.CENTER);
    jfrm.getContentPane().add(pane); 
      
      
      /*==================================*/
      /* Create the more details panel. */
      /*==================================*/
      
       JPanel moreDetailsPanel = new JPanel(); 
	   moreDetailsPanel.setLayout(new GridLayout(2,1));
	   
	         	moreDetailsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "More Details", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, new Font("Cambria", Font.ITALIC, 16)));

	   
	  moreDetails = new JTextArea();
	   moreDetails.setEditable(false);
	  // moreDetails.setOpaque(false);
	   moreDetails.setLineWrap(true);
	   moreDetails.setWrapStyleWord(true);
	   	  // moreDetailsPanel.setPreferredSize(new Dimension(100,300));
	   
	   
	   
	   
	 //  moreDetailsPanel.add(moreDetails);
	   JScrollPane moreDetailsScroll = new JScrollPane(moreDetails);
	

	moreDetailsPanel.add(moreDetailsScroll);
	


	   
	    imgLabel = new JLabel(new ImageIcon());
	    moreDetailsPanel.add(imgLabel);


		
		/*==============================================*/
      /* Create a panel including the more details area and
      	 add it to the content pane.  */
      /*==============================================*/

      
   //   jfrm.getContentPane().add(moreDetailsPanel, BorderLayout.EAST);
    jfrm.getContentPane().add(moreDetailsPanel);
	      
    
      /*===================================================*/
      /* Initially select the first item in each ComboBox. */
      /*===================================================*/
       
      preferredActor.setSelectedIndex(0); 
      preferredYear.setSelectedIndex(0); 
      preferredGenre.setSelectedIndex(0); 
      
      
      
      /*=================================*/
      /* Customise look and feel. 		 */
      /*=================================*/
      
      jfrm.getContentPane().setBackground(Color.WHITE);
      preferencesPanel.setOpaque(false);
      preferencesPanelTh.setOpaque(false);
      choicesPanel.setOpaque(false);
      pane.setOpaque(false);
      moreDetailsPanel.setOpaque(false);
      randomPanel.setOpaque(false);
      dvdRadio.setOpaque(false);
      theatreRadio.setOpaque(false);
      dvdRadio.setAlignmentX(Component.CENTER_ALIGNMENT);
      theatreRadio.setAlignmentX(Component.CENTER_ALIGNMENT);
      preferencesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
      preferencesPanelTh.setAlignmentX(Component.CENTER_ALIGNMENT);
      randomPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
      
      
      
      
     /*  table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
        public void valueChanged(ListSelectionEvent event) {
            // do some actions here, for example
            // print first column value from selected row
           // System.out.println(table.getValueAt(table.getSelectedRow(), 0).toString());
            
            String movieName = (String)table.getModel().getValueAt(table.getSelectedRow(), 0);
        StringTokenizer st = new StringTokenizer(movieName,"\n");
        	String  myMovie = st.nextToken();
        	
        	try
        	{
        	fillMovieDetails(myMovie);
        	}catch(Exception e)
        	{
        	
        	}
        

        }
    });*/

			 table.addMouseListener(new java.awt.event.MouseAdapter()
            {
			public void mouseClicked(java.awt.event.MouseEvent e)
			{
				int row=table.rowAtPoint(e.getPoint());
				int col= table.columnAtPoint(e.getPoint());
			  String movieTitle = table.getValueAt(row,col).toString();
			  
			  try
			  {
			  if(!movieTitle.equals("1")) fillMovieDetails(movieTitle);
			  }catch(Exception exc)
			  {
			  System.out.println(exc.toString());
			  }
			}	}
);
      
      jfrm.pack();
      jfrm.setVisible(true);
      
      /*========================*/
      /* Load the movie program. */
      /*========================*/
      
      clips = new Environment();
      
      clips.load("movie.clp");
      //clips.watch("facts");
      //clips.watch("rules");
      //clips.watch("activations");
      clips.reset();
      
      try
        { runMovie(0); }
      catch (Exception e)
        { e.printStackTrace(); }
       
      /*====================*/
      /* Display the frame. */
      /*====================*/

            jfrm.setExtendedState(Frame.MAXIMIZED_BOTH);  

      jfrm.pack();

      jfrm.setVisible(true);  
     }  
     
     
    
 
   /*########################*/
   /* ActionListener Methods */
   /*########################*/

   /*******************/
   /* actionPerformed */
   /*******************/  
   public void actionPerformed(ActionEvent ae) 
     { 
      if (clips == null) return;
    
     if(ae.getSource() == randomButton)
     {
      	isRandom=1;
      /*	preferredGenre.setSelectedIndex(0);
      	preferredActor.setSelectedIndex(0);
      	preferredYear.setSelectedIndex(0);
      	preferredLocation.setSelectedIndex(0);
      	preferredDirector.setSelectedIndex(0);
      	preferredMpaa.setSelectedIndex(0);*/
      }
      else if(ae.getActionCommand().equals("DVDMode"))
      {
		isRandom=0;
      
      enableComponents(preferencesPanel, true);
      enableComponents(preferencesPanelTh, false);
     // movieList.setRowCount(0);
      //   clips.reset();

      String assertDVD = "(preference (name mode) (value DVD))";
    System.out.println(assertDVD);
	 clips.assertString(assertDVD);
     /* String assertReset = "(retract (nth$ 1 (find-all-facts ((?f preference)) (eq ?f:name location))))";
       System.out.println(assertReset);
      clips.assertString(assertReset);
     assertReset = "(retract (nth$ 1 (find-all-facts ((?f preference)) (eq ?f:name genre))))";
       System.out.println(assertReset);
      clips.assertString(assertReset);*/
	  isDVD=1;
	  isTheatre=0;
	  //clips.assertString(assertReset);
      }
      
      else if(ae.getActionCommand().equals("TheatreMode"))
      {
		isRandom=0;
      

      enableComponents(preferencesPanel, false);
      enableComponents(preferencesPanelTh, true);
            //	clips.reset();

      	String assertTheater = "(preference (name mode) (value theatre))";
      System.out.println(assertTheater);
	 clips.assertString(assertTheater);
     // movieList.setRowCount(0);
      //String assertReset = "(preference (name location))";
      //System.out.println(assertReset);
      isDVD=0;
      isTheatre=1;
	//  clips.assertString(assertReset);
      }
	  
	  if(dvdRadio.isSelected()){
		  isDVD=1;
		  isTheatre=0;
	  }
	  if(theatreRadio.isSelected()){
		  isDVD=0;
		  isTheatre=1;
	  }
    
    
        
      try
        { runMovie(isRandom); }
      catch (Exception e)
        { e.printStackTrace(); }
      }
     
     public void enableComponents(Container container, boolean enable) {
        Component[] components = container.getComponents();
        for (Component component : components) {
            component.setEnabled(enable);
            
            if (component instanceof Container) {
                enableComponents((Container)component, enable);
            }
            if(enable==false && component instanceof JComboBox)
            	((JComboBox) component).setSelectedIndex(0);
        }
     }

     
   /***********/
   /* runMovie */
   /***********/  
   private void runMovie(int rnd) throws Exception
     { 
      String item;
      //final int isRandom = rnd;
      //final int isDVD=dvd;
      //final int isTheatre=theatre;      
      int yearSelected=0, genreSelected=0, actorSelected=0, directorSelected=0, MpaaSelected=0, locationSelected=0, genreThSelected=0;
      
      preferredActorNames = buildStringArray("list_actors.txt");
      preferredYearNames = buildStringArray("list_years.txt");
      preferredGenreNames = buildStringArray("list_genre.txt");
      preferredDirectorNames = buildStringArray("list_director.txt");
      if (isExecuting) return;
      
	  //System.out.println(" mode " + isDVD + " " + isTheatre);
      //System.out.println("FINE 0");
      
      if(isDVD == 1 && isTheatre==0)
      {
      
        clips.reset();      

     /* String assertReset = "(retract (nth$ 1 (find-all-facts ((?f preference)) (eq ?f:name location))))";
       .println(assertReset);
      clips.assertString(assertReset);
     assertReset = "(retract (nth$ 1 (find-all-facts ((?f preference)) (eq ?f:name genre))))";
       System.out.println(assertReset);
      clips.assertString(assertReset);*/
      //System.out.println("FINE 1");
    String assertDVD = "(preference (name mode) (value DVD))";
    //System.out.println(assertDVD+"2");
	 clips.assertString(assertDVD);
      //System.out.println("FINE 2");

      if(preferredYear.getSelectedIndex()!=0)
      	yearSelected=1;
      else
      	yearSelected=0;      
      item = preferredYearNames[preferredYear.getSelectedIndex()];
      //System.out.println("FINE 3");
      if(preferredYear.getSelectedIndex()!=0)
	  {
      String assertYearStr = "(preference (name year) (value "+item+"))";
      System.out.println(assertYearStr);
	  clips.assertString(assertYearStr);
	  }
      //System.out.println("FINE 4");
	  
	  if(preferredGenre.getSelectedIndex()!=0)
      	genreSelected=1;
      else
      	genreSelected=0;  
	  item = preferredGenreNames[preferredGenre.getSelectedIndex()];
      //System.out.println("FINE 5");
	  if(preferredGenre.getSelectedIndex()!=0)
	{
      String assertGenreStr = "(preference (name genre) (value "+item+"))";
      System.out.println(assertGenreStr);
	  clips.assertString(assertGenreStr);
	  }
      //System.out.println("FINE 6");
	  
	  if(preferredActor.getSelectedIndex()!=0)
      	actorSelected=1;
      else
      	actorSelected=0;  
      item = preferredActorNames[preferredActor.getSelectedIndex()];
      if(preferredActor.getSelectedIndex()!=0)
      {
      String assertActorStr = "(preference (name actor) (value \""+item+"\"))";
      System.out.println(assertActorStr);
      
      clips.assertString(assertActorStr);
      }
        
      if(preferredDirector.getSelectedIndex()!=0)
      	directorSelected=1;
      else
      	directorSelected=0;  
      item = preferredDirectorNames[preferredDirector.getSelectedIndex()];
      if(preferredDirector.getSelectedIndex()!=0)
      {
      String assertDirectorStr = "(preference (name director) (value \""+item+"\"))";
      System.out.println(assertDirectorStr);
      clips.assertString(assertDirectorStr);
      }
      
      if(preferredMpaa.getSelectedIndex()!=0)
      	MpaaSelected=1;
      else
      	MpaaSelected=0;  
      item = preferredMpaaNames[preferredMpaa.getSelectedIndex()];
      if(preferredMpaa.getSelectedIndex()!=0)
      {
      String assertMpaaStr = "(preference (name mpaa) (value "+item+"))";
      System.out.println(assertMpaaStr);
      
      clips.assertString(assertMpaaStr);
      }
      
      totalCriteriaSelected = yearSelected + genreSelected + actorSelected + directorSelected + MpaaSelected;
      
      }
      else if(isDVD==0 && isTheatre==1)
      {
		
          clips.reset();      
     
      //System.out.println("FINE 2");
     String assertTheater = "(preference (name mode) (value theatre))";
     // System.out.println(assertTheater+"2");
	 clips.assertString(assertTheater);
	  
      if(preferredLocation.getSelectedIndex()!=0)
      	locationSelected=1;
      else
      	locationSelected=0;  
      item = preferredLocationNames[preferredLocation.getSelectedIndex()];
      
      if(preferredLocation.getSelectedIndex()!=0)
      {
      System.out.println(preferredLocation.getSelectedIndex());
      String assertLocationStr = "(preference (name location) (value "+item+"))";
      System.out.println(assertLocationStr);
      clips.assertString(assertLocationStr);
      }
      
       if(preferredGenreTh.getSelectedIndex()!=0)
      	genreThSelected=1;
      else
      	genreThSelected=0;  
	  item = preferredGenreNames[preferredGenreTh.getSelectedIndex()];
      
      if(preferredGenreTh.getSelectedIndex()!=0){
      
      System.out.println(preferredGenreTh.getSelectedIndex());
	  String assertGenreStr = "(preference (name genre) (value "+item+"))";
      System.out.println(assertGenreStr);
      clips.assertString(assertGenreStr);
	  }
	  totalCriteriaSelected = locationSelected + genreThSelected;
	  }
      
      
      
      Runnable runThread = 
         new Runnable()
           {
            public void run()
              {
               clips.run();
               
               SwingUtilities.invokeLater(
                  new Runnable()
                    {
                     public void run()
                       {
                        try 
                          { updateMovies(isRandom); }
                        catch (Exception e)
                          { e.printStackTrace(); }
                       }
                    });
              }
           };
      
      isExecuting = true;
      
      executionThread = new Thread(runThread);
      
      executionThread.start();
     }
     
   /***************/
   /* updateMovies */
   /***************/  
   private void updateMovies(int isRandom) throws Exception
     { 
     
  	 String evalStr;
  	 PrimitiveValue pv;
  	 movieMap = new HashMap<String, String>();
  	 if(isRandom==0)
  	 {
  	 
  	
  	 evalStr= "(get-movie-list)";
  	  pv = clips.eval(evalStr);

	 
	 renderer.setMaximum(totalCriteriaSelected);
      
      movieList.setRowCount(0);
      
	  for (int i = 0; i < pv.size(); i++) 
        {
         PrimitiveValue fv = pv.get(i);

         int certainty = fv.getFactSlot("con").numberValue().intValue(); 
                  
         String movieName = fv.getFactSlot("title").stringValue();
         
       String criteriaName = fv.getFactSlot("matched").toString();
       
       movieMap.put(movieName, criteriaName);
       
       //System.out.println(criteriaName+"");
         
        // System.out.println((fv.getFactSlot("matched"))+"");
                  
         movieList.addRow(new Object[] { movieName, new Integer(certainty) });
        
        }
  	 }
  	 else
  	 {
  	 evalStr="(get-random)";
  	 pv = clips.eval(evalStr);
  	 renderer.setMaximum(1);
  	       movieList.setRowCount(0);

         int certainty = 1; 
                  
         String movieName = pv.getFactSlot("title").stringValue();
         
         
                  
         movieList.addRow(new Object[] { movieName, new Integer(certainty) });
         
         clips.reset();



  	 }
                                       
	 
     
        
      jfrm.pack();
      jfrm.setExtendedState(Frame.MAXIMIZED_BOTH);  
      executionThread = null;
      
      isExecuting = false;
     } 
     
     
    /**********************************************/
   /* buildActorArray: insert into String Array */
   /***********************************************/ 
    
    private String[] buildStringArray(String filename) throws Exception
    {
    	String arr[];
        java.util.List<String> namesList = new ArrayList<String>();
        
        
        FileInputStream fstream_names = new FileInputStream(filename); 
        DataInputStream data_input = new DataInputStream(fstream_names); 
        BufferedReader buffer = new BufferedReader(new InputStreamReader(data_input)); 
        String str_line; 

        while ((str_line = buffer.readLine()) != null) 
        { 
            str_line = str_line.trim(); 
            if ((str_line.length()!=0))  
            { 
                namesList.add(str_line);
            } 
        }

        arr = (String[])namesList.toArray(new String[namesList.size()]);
    	
    	return arr;
    
    }    
    
    /****************************************/
   /* get all details of the movie selected */
   /****************************************/
   
   private void fillMovieDetails(String title) throws Exception
   {
   
   String movieCriteria=" ";
   String evalStr = "(get-movie-by-title \""+title+"\")";
   System.out.println(evalStr);
  PrimitiveValue fv = clips.eval(evalStr);
  
 
  //PrimitiveValue fv = pv.get(0);
    String movieName = fv.getFactSlot("title").stringValue();
       //System.out.println(movieName+"1");

    
    int movieYear = fv.getFactSlot("year").numberValue().intValue();
    String movieGenre = fv.getFactSlot("genre").toString();
    String movieActor =  fv.getFactSlot("actor").toString();
    String movieDirector = fv.getFactSlot("director").toString();
    int movieRating = fv.getFactSlot("rating").numberValue().intValue();
    String movieMpaa = fv.getFactSlot("mpaa").toString();
    String movieStory = fv.getFactSlot("storyline").stringValue();
    String movieMode = fv.getFactSlot("mode").toString();
    String movieURL = fv.getFactSlot("imageURL").stringValue();
    
          // System.out.println(isRandom+"");

    
    if(isRandom==0)
    {
    String criteria = movieMap.get(movieName);
    
    StringTokenizer st = new StringTokenizer(criteria);
    st.nextToken();
    while(st.hasMoreTokens())
    	movieCriteria = movieCriteria +  " | " + st.nextToken();
    	movieCriteria = movieCriteria.substring(0, movieCriteria.length()-1);
    	movieCriteria +=" | ";
    	
    	         //  System.out.println(movieName+"2");
	}
    	 System.out.println(movieName +"\n"+ movieYear+"\n" + movieGenre +"\n"+ movieActor + "\n"+ movieDirector +"\n"+
    movieRating+ "\n" + movieMpaa+ "\n" + movieStory +"\n" + movieMode +"\n" + movieURL + "\n"+ movieCriteria);
    
	   moreDetails.setText("Criteria Matched: " + movieCriteria + "\n\n");
	   moreDetails.append("Title: "+movieName+"\n\n");
	   moreDetails.append("Year of Release: "+movieYear +"\n\n");
	   moreDetails.append("Genre: "+ movieGenre+"\n\n");
	   moreDetails.append("IMDb View Rating: "+ movieRating +"\n\n");
	   moreDetails.append("MPAA Rating: "+ movieMpaa +"\n\n");
	   moreDetails.append("Cast: "+ movieActor +"\n\n");
	   moreDetails.append("Director: "+ movieDirector +"\n\n");
	   moreDetails.append("Available in: "+ movieMode +"\n\n");
	   moreDetails.append("Plot: "+ movieStory +"\n\n");
	   
	   URL url = new URL(movieURL);
	   Image image = null;
        try {
            image = ImageIO.read(url);
            imgLabel.setIcon(new ImageIcon(image));

        } catch (IOException e) {
        	e.printStackTrace();
        }


   }
    
     
   /********/
   /* main */
   /********/  
   public static void main(String args[])
     {  
      /*===================================================*/
      /* Create the frame on the event dispatching thread. */
      /*===================================================*/
      

      SwingUtilities.invokeLater(
        new Runnable() 
          {  
           public void run() { new MovieDemo(); }  
          });   
     }  

  }