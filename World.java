

import java.io.FileNotFoundException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.invoke.MethodHandles;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ListIterator;
import java.lang.reflect.*;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors.*;

enum Digestion{

    
    CARNIVORE,
    HERBIVORE,
    NONE


}


// enum ActionType {
//     CREATE, DELETE, MOVE, REPRODUCE, PRINT, MAKE_TURN
// }

class Types{
    enum ActionType{ATTACK, CREATE, DELETE, MOVE, REPRODUCE, PRINT, MAKE_TURN, INIT_GAME, GET_STATE}
    enum Digestion{CARNIVORE, HERBIVORE, NONE}
    enum ResponseType{   }

}


class Controller {
 
    boolean quit;
    Dispatcher dispatcher;
 
 
    ArrayList<Action> actions;

    Controller(Dispatcher dispatcher){
        this.dispatcher = dispatcher;
        this.quit = false;
    }
    Controller() {
        this.quit = false;

    }

    void startup(Config config){
        for(Action action : config.actions){
             this.dispatcher.dispatch(action);
        }
    }
    void show(){
        this.dispatcher.dispatch(new Action(Types.ActionType.PRINT));
    }


 
    void makeMove(){
    Action action =null;
        while(action==null)
        {
         action = this.dispatcher.getNext().getAction();
        }
        
      this.dispatcher.dispatch( action );
    }
    
 
 
}
class Response{
    HashMap<String, String> data;

    Response(){
        this.data = new HashMap<String, String>();
    }

    void getOrganismData(int count){
        this.data.put("organism_count", Integer.toString(count)) ;
    }
}
abstract class DefaultConfig{
    int gridSize = 25;
    int plantsCount = 60;
    int carnivoreCount = 15;
    int herbivoreCount = 25;
    ArrayList<Action> actions;
 
}
class Config extends DefaultConfig{

    Config(){
        
        this.actions = new ArrayList<Action>();

    }
    void setActions(OrganismFactory factory, ZoneManager zones){
        
        for(int i =0; i< plantsCount; i++){

            this.actions.add( new Action(    factory.getPlant(),  zones.getRandom("free") ) );

        }

        for(int i=0; i<carnivoreCount; i++){
            this.actions.add(new Action(factory.getCarnivore(), zones.getRandom("free")));
        }

        for(int i=0; i< herbivoreCount; i++){
            this.actions.add(new Action(factory.getHerbivore(), zones.getRandom("free")));
        }

    }
}
 abstract class RandomGenerator{
    static int generate() {
        int offset = 1;
        return (0 - offset) + (int) (Math.random() * ((offset - (0 - offset)) + 1));
    }
    static int generate(int offset) {
        return (0 - offset) + (int) (Math.random() * ((offset - (0 - offset)) + 1));
    }
    static int generate(int min, int max) {
        Random r = new Random();


        if (min > max) {
            return generate(max, min);
    
        }
   
        return r.nextInt((max - min) + 1) + min;
    }
 

    static Zone generate(Zone zone){
        System.out.println(zone);
        return zone;
    }

    static Zone generate(ArrayList<Zone> zone){
        return zone.get(RandomGenerator.generate( 0,  zone.size()-1 ) );
    }

 
 
 
    
 
}
 
 
// factory.supply(Tree.getSupply());
public class World{

    public static void main(String[] args){



        Dispatcher dispatcher = new Dispatcher();

        Controller controller = new Controller(dispatcher);
        ZoneManager zoneManager = new ZoneManager(dispatcher);

        OrganismManager organismManager = new OrganismManager(dispatcher);

        OrganismFactory factory =  new OrganismFactory(dispatcher);

        factory.supply(Grass.getSupply());

        factory.supply(Wolf.getSupply());
        factory.supply(Sheep.getSupply());

        Config config = new Config();
        config.setActions(factory, zoneManager);


        controller.startup(config);
 
        // System.out.print("\033[H\033[2J");
        // controller.show();

        while(true){

           
  
            controller.makeMove();
            System.out.print("\033[H\033[2J");
             controller.show();
            try {
                Thread.sleep(400);
            } catch (Exception e) {

            }

        }




    }

 
}


class Clock extends Thread{
    
}
 
class Zone {
    char x;
    int y;
    boolean isOccupied ;
    boolean hasAnimal;
    boolean hasPlant;
    Animal animal;
    Plant plant;
    Organism organism;
    HashMap<String, Zone> neighbours;
    Zone(char x, int y) {
        this.x = x;
        this.y = y;
        this.isOccupied = false;
        this.hasAnimal = false;
        this.hasPlant = false;
        this.animal = null;
        this.plant = null;
        this.organism = null;
        this.neighbours = null;
   
    }

    void setNeighbours(HashMap<String, Zone> neighbours){
        this.neighbours = neighbours;
    }
    String appear(){
        if(this.organism!= null){
            
            if(this.y<10) return String.format(this.organism.id+"  ");
            else return String.format(this.organism.id+"  ");
        }
        else{
            return this.toString();
        }
    };

    String showOrganism(){
        if(this.organism!=null){
            if(this.y<10) return String.format(this.organism.id+ "  ");
            else return String.format(this.organism.id+"  ");
        }
        else{
            return "    ";
        }
    }
    void subscribe(Organism organism){
        this.isOccupied = true;
        this.organism = organism;

    }
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        if (this.y < 10)
            return String.format("%d%s  ", y, x);
        else
            return String.format("%d%s ", y, x);
    }

    String Alert(){
        String ANSI_RED = "\u001B[31m";
        String ANSI_GREEN = "\u001B[32m";

        String ANSI_YELLOW = "\u001b[33m";
        String ANSI_BLUE = "\u001b[34m";
        String ANSI_MAGENTA = "\u001b[35m";
        String ANSI_CYAN = "\u001b[36m";
        String ANSI_WHITE = "\u001b[37m";


        String ANSI_RESET = "\u001B[0m";
        if(this.y <10)
            return ANSI_RED + y + x +"  "+ ANSI_RESET;
        return ANSI_RED + y +  x +" "+ ANSI_RESET;
    }

    int[][] getMatrix() {
        int[][] result = { { 1, 2, 3 }, { 12, 3, 4 }, { 1, 2, 3 } };
        return result;
    }

}
 
interface Subscriber<Dispatcher>{
    Dispatcher set(Dispatcher dispatcher);
}
class ZoneManager {
    ArrayList<Zone> zones;
    ArrayList<Zone> empty;
    Dispatcher dispatcher;

    int GRIDSIZE = 25;
    ZoneManager(){
         
        this.zones = new ArrayList<Zone>();
        this.empty = new ArrayList<Zone>();
         
        this.setZones(  );
    }

    ZoneManager(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
        this.dispatcher.subscribe(this);
 
        this.zones = new ArrayList<Zone>();
        this.empty = new ArrayList<Zone>();
    
        this.setZones(dispatcher);
        this.setNeighbours();



    }
        void setZones(   ){
        for (int y = GRIDSIZE; y > 0; y--)  

            for (int x = 65; x <= 65 + GRIDSIZE; x++) 
            {
                Zone content = new Zone((char) x, y);
                this.zones.add(content);
                this.empty.add(content);
                 
            }
        }
    void setZones(Dispatcher dispatcher){
        for (int y = GRIDSIZE; y > 0; y--)  

            for (int x = 65; x <= 65 + GRIDSIZE; x++) 
            {
                Zone content = new Zone((char) x, y);
                this.zones.add(content);
                this.empty.add(content);
                  dispatcher.subscribe(content)  ;
            }
        }
   

    void setNeighbours(){
        for(Zone zone : this.zones){
            Zone top, bottom, left, right;

            HashMap<String, Zone> neighbours = new HashMap<String, Zone>();


            if((int)zone.x==65  ) left = null;
            else left = this.getZone(      (char) ( (int) zone.x-1 ), zone.y  );
            if( (int) zone.x == 65+GRIDSIZE ) right = null;
            else right = this.getZone( (char) ( (int) zone.x+1), zone.y);
            if( zone.y ==1 ) bottom = null;
            else bottom = this.getZone(  zone.x,  zone.y-1);
            if (zone.y==GRIDSIZE) top = null;
            else top = this.getZone( zone.x, zone.y+1);

            neighbours.put("top", top);
            neighbours.put("bottom", bottom);
            neighbours.put("left", left);
            neighbours.put("right", right);

            zone.setNeighbours(neighbours);
      

        }
    }
    void printTerritory(ArrayList<Zone> zones){
        for(Zone el : this.zones){

            if(zones.contains(el) ){
                System.out.print(el.Alert());    
            } 
            else{
            System.out.print(el);
            }
            if( (int) el.x-65 ==this.GRIDSIZE){
                System.out.println();
            }
        }
    }
    Zone getZone(char x, int y){
         return this.zones.stream().filter(el-> el.x==x && el.y==y ).reduce( (a,b) -> { throw new IllegalStateException("Multiple elements"); } ).get();
    }
    void printOrganisms(){
        for (Zone el : this.zones) {

            System.out.print(el.appear());
            if ((int) el.x - 65 == this.GRIDSIZE)
                System.out.println();

        }
    }
    void printOnly(){
        for (Zone el : this.zones) {

            System.out.print(el.showOrganism());
            if ((int) el.x - 65 == this.GRIDSIZE)
                System.out.println();

        }
    }


    Zone getZone(Zone zone){


        return this.zones.stream().filter(x-> x.hashCode()==zone.hashCode() ).collect(Collectors.toCollection(ArrayList::new)).get(0) ;
    }

    void subscribe(Organism organism, Zone zone){
        
        getZone(zone).subscribe(organism);
        
        

        

    }

    Zone getRandom(String extra){
        return this.empty.get(RandomGenerator.generate(0, GRIDSIZE * GRIDSIZE));
    }

    Zone getRandom() {

        return this.zones.get(RandomGenerator.generate(0, GRIDSIZE * GRIDSIZE));
    }    
 
    Zone selectRandom(){
        Zone zone  = this.zones.get(RandomGenerator.generate(0,GRIDSIZE* GRIDSIZE) ) ;
        for (Zone el : this.zones) {

            if(zone.hashCode() == el.hashCode() ){
                System.out.print(el.Alert());
            }
            else{
                System.out.print(el);
            }
            if ((int) el.x - 65 == this.GRIDSIZE)
                System.out.println();

        }
        return zone;
    }

    ArrayList<Zone> selectRandom(int count){
        ArrayList<Zone> selected = new ArrayList<Zone>();
        for(int i=0; i<count; i++){
            Zone zone = this.zones.get(RandomGenerator.generate(0, GRIDSIZE * GRIDSIZE));
            selected.add(zone);
        }
        // for ( Zone el : this.zones){

        //     if ( selected.contains(el)  ){ 

        //         System.out.print(el.Alert() );

        //      }
        //      else{
        //             System.out.print(el);
        //      }
        //     if ((int) el.x - 65 == this.GRIDSIZE)
        //         System.out.println();
        // }
        return selected;
        }  
    void printRows() {
        int count = 0;
        for (Zone el : this.zones) {

            System.out.print(el);
            if ((int) el.x - 65 == this.GRIDSIZE)
                System.out.println();

        }
    }
}
class OrganismManager{
    ArrayList<Organism> organisms;
    ListIterator<Organism> iterator;
 
    ArrayList<Animal> animalSpecies;
    ArrayList<Plant> plantSpecies;
    Dispatcher dispatcher ;
    OrganismManager(){
        this.animalSpecies = new ArrayList<Animal>();
        this.plantSpecies = new ArrayList<Plant>();
        this.organisms = new ArrayList<Organism>();
    }
    OrganismManager(Dispatcher dispatcher){
        this.dispatcher = dispatcher;
        this.dispatcher.subscribe(this);
        this.animalSpecies = new ArrayList<Animal>();
        this.plantSpecies = new ArrayList<Plant>();
        this.organisms = new ArrayList<Organism>();
        
    }
    void add(Organism organism){
        this.organisms.add(organism);
    }
    void setIterator(){
        this.iterator = this.organisms.listIterator();
    }
    Organism next(){
        try{
            return this.iterator.next();
         }
        catch(Exception e){
            this.setIterator();
            return this.next();
        }
    }

    void makeMove(){
        for(Organism organism : this.organisms){
            organism.makeMove();
        }
        
    }
    Response getState(){
        Response response = new Response();
        
        return response;
    }
    int getCount(){
        return this.organisms.size();
    }
    void organismMove(Organism organism, Zone zone){
        
    }
    void deleteOrganism(Organism organism){
        ListIterator<Organism> itr = this.organisms.listIterator();
        while(itr.hasNext() ){
            Organism cur = itr.next();
            if(organism.hashCode()==cur.hashCode() ){
                itr.remove();
            }
        }
        organism = null;
    }
    void printStats(){
        System.out.println();
        System.out.print("Carnivores :" + this.getCarnivores() );
        System.out.print("Herbivores :" + this.getHerbivores());
        System.out.print("Plants :" + this.getPlants());

    }

    int getCarnivores(){
       return this.organisms.stream().filter(el->el instanceof Carnivore).collect(Collectors.toCollection(ArrayList::new)).size();
    }
    int getHerbivores(){
       return this.organisms.stream().filter(el->el instanceof Herbivore).collect(Collectors.toCollection(ArrayList::new)).size();
    }
    int getPlants(){
       return this.organisms.stream().filter(el->el instanceof Plant).collect(Collectors.toCollection(ArrayList::new)).size();
    }
 
}
abstract class Organism {
    static String ANSI_RED = "\u001B[31m";
    static String ANSI_GREEN = "\u001B[32m";
    static Digestion digestion;
    static String ANSI_YELLOW = "\u001b[33m";
    static String ANSI_BLUE = "\u001b[34m";
    static String ANSI_MAGENTA = "\u001b[35m";
    static String ANSI_CYAN = "\u001b[36m";
    static String ANSI_WHITE = "\u001b[37m";

    static String ANSI_RESET = "\u001B[0m";
    static HashMap<String, String> colors = new HashMap<String, String>();

  static{ 
       colors.put("red", ANSI_RED); 
       colors.put("green", ANSI_GREEN);
       colors.put("yellow", ANSI_YELLOW);
       colors.put("blue", ANSI_BLUE);
       colors.put("magenta", ANSI_MAGENTA);
       colors.put("cyan", ANSI_CYAN);
       colors.put("white", ANSI_WHITE);
       colors.put("reset", ANSI_RESET);
    
    }



    
    //static Digestion digestion;
    String id;
 
    Zone zone;
 
    static ArrayList<Organism> prototypes;

 
    void setZone(Zone zone){
 
        this.zone = zone;
    }
    Organism(){
    
    }
 

    ArrayList<Zone> getReach() {
        return this.getExtention(this.range, new ArrayList<Zone>(Arrays.asList(this.zone)), new ArrayList<Zone>());
    }

    ArrayList<Zone> getExtention(int range, ArrayList<Zone> zones, ArrayList<Zone> territory) {
        if (range <= 0 && territory.size()==0) {
            return territory;
          
        }

        if (range<=0){
            return territory;
        }

        for (Zone zone : zones) {
            for (Zone neighbour : zone.neighbours.values()) {
                if (neighbour != null && territory.contains(neighbour) == false && neighbour.hashCode() != this.zone.hashCode()) {
                    territory.add(neighbour);
                } else {

                }
            }
        }
        ArrayList<Zone> toCheck = new ArrayList<Zone>();
        toCheck.addAll(territory); 
        // for(Zone zone : zones){
        // for(Zone inner : zone.neighbours.values() ){
        // if(inner !=null && territory.contains(inner)==false ){
        // territory.add(inner);

        // }
        // }

        // }
        return this.getExtention(range - 1, toCheck, territory);

    }
    Action makeMove(){
        return this.getAction();
   
        
    }

    Action getAction(){
        Object decision = this.getDecision();
        if(decision instanceof Zone){
            return new Action(this, (Zone) decision );
        }
        if(decision instanceof Animal){
            return new Action(this, (Organism) decision);
        }
        else return null;
    }


 
    Object getDecision() {
 
 
        return null;
        
    }
    ArrayList<?> getDecisions(){
        return this.getSpecifics(this.getReach());
    }
  
    ArrayList<?> getSpecifics(ArrayList<Zone> territory) {
        return territory.stream().map(el -> { if(el.organism!=null)return el.organism ; else return el;  }).collect(Collectors.toCollection(ArrayList::new));
    }


    // ArrayList<Organism> getSpecifics(ArrayList<Zone> territory){
    //     return territory.stream().map(el->el.organism).collect(Collectors.toCollection(ArrayList::new));
    // }

    ArrayList<?> getNeighbours(){
        return this.getSpecifics(this.getReach());
    }


    String getId(){
        return this.id;
    }

    static int range;      

}
abstract class Animal extends Organism  {

    Animal(){}

 
    void occupy(Zone zone){
        this.zone = zone;
    }
    

}
abstract class Carnivore extends Animal{
    static Digestion digestion = Digestion.CARNIVORE;
    static String classColor = colors.get("red");
    
    Carnivore(){
        super();
       
    }
 
    static int getRange() {
        return range;
    }

    static Digestion getDigestion() {
        return digestion;
    }

    Object getDecision(){
      
            try {
                return this.getDecisions().stream().filter(x -> x instanceof Herbivore).findFirst().get();
            } catch (Exception e) {
                return this.getDecisions().get(RandomGenerator.generate(0, this.getDecisions().size() - 1));
            }
       
    }
    // ArrayList<Zone> filterMoves(ArrayList<Zone> zones){


    // }

    // ArrayList<Organism> getDecisions() {

        
      

    //  return this.getSpecifics(this.getReach()).stream().filter(el -> {return el instanceof Herbivore || el==null;}).collect(Collectors.toCollection(ArrayList::new));

       
 

      
    // }
}

abstract class Herbivore extends Animal {
    static Digestion digestion = Digestion.HERBIVORE;
    static String classColor = colors.get("blue");
    Herbivore(){
        super();
 
    }
 
    
    static int getRange() {
        return range;
    }

    static Digestion getDigestion() {
        return digestion;
    }
    
    Object getDecision() {

     
            try {
                return this.getDecisions().stream().filter(x -> x instanceof Plant).findFirst().get();
            } catch (Exception e) {
                return this.getDecisions().get(RandomGenerator.generate(0, this.getDecisions().size() - 1));
            }

      
 
    }
 
    // ArrayList<Organism> getDecisions() {
    //  return this.getSpecifics(this.getReach()).stream().filter(el -> {return el instanceof Plant || el==null;}).collect(Collectors.toCollection(ArrayList::new));
    // }
}
abstract class Plant  extends Organism  {
     static Digestion digestion = Digestion.NONE;
    static String classColor = colors.get("green");
    Plant(){
       
    }
 
 
    static int getRange() {
        return range;
    }

    static Digestion getDigestion() {
        return digestion;
    }
    
    Object getDecision() {

 

        return null;

    }
}


//@FunctionalInterface
interface Supplier<T>{
    T get(Zone zone);
}


class Supply{

    Supplier<Organism> supplier;
    Digestion digestion;
    int range;
    String className;
    Supply(Supplier<Organism> supplier, Digestion digestion, int range, String className){

        this.supplier = supplier;
        this.digestion = digestion;
        this.range = range;
        this.className = className;


      
    }

}

 
class Wolf extends Carnivore{
    
     static int range   = 3;
    
    Wolf() {
        this.id = classColor + "WF" + colors.get("reset");
        this.range = 3;
    
    }
    Wolf(Zone zone){
        
        this.zone = zone;
        this.zone.subscribe(this);
        this.id = classColor + "WF" + colors.get("reset");
    }
 

    ArrayList<Zone> getReach() {
        return this.getExtention(this.range, new ArrayList<Zone>(Arrays.asList(this.zone)), new ArrayList<Zone>());
    }

     
    private static Supplier<Organism> getSupplier(){
        
        Supplier<Organism> mkWolf = (Zone zone) -> new Wolf(zone);
        return mkWolf;
    }  

    static Supply getSupply(){
        return new Supply(getSupplier(), getDigestion(), getRange(),  MethodHandles.lookup().lookupClass().getSimpleName() );
    } 
}

class Sheep extends Herbivore{
   int range =1 ;
   
    Sheep(){
        this.id = classColor + "SP" + colors.get("reset");
        this.range = 1;
        //System.out.print("Creating sheep");
    }
    Sheep(Zone zone){
        this.range =1;
        this.zone = zone;
        this.id = classColor + "SP" + colors.get("reset");
        this.zone.subscribe(this);
    }
 

    ArrayList<Zone> getReach() {
        return this.getExtention(this.range, new ArrayList<Zone>(Arrays.asList(this.zone)), new ArrayList<Zone>());
    }
   
    private static Supplier<Organism> getSupplier() {

        Supplier<Organism> mkSheep = (Zone zone) -> new Sheep(zone);
        return mkSheep;
    }
    
    static Supply getSupply() {
        return new Supply(getSupplier(), getDigestion(), getRange(),  MethodHandles.lookup().lookupClass().getSimpleName() );
    }
 
}
 
class Grass extends Plant {
    int range = 0;
    Grass(){
        this.id = classColor + "GS" + colors.get("reset");
        this.range = 0;
    }
 
    Grass(Zone zone){
        this.zone = zone;
        this.zone.subscribe(this);
        this.id = classColor + "GS" + colors.get("reset");
    }

 
    
    ArrayList<Zone> getReach() {
        return this.getExtention(this.range, new ArrayList<Zone>(Arrays.asList(this.zone)), new ArrayList<Zone>());
    }
     static Supplier<Organism> getSupplier() {

        Supplier<Organism> mkGrass = (Zone zone) -> new Grass(zone);
        return mkGrass;
    }

    static Supply getSupply() {
       return new Supply(getSupplier(), getDigestion(), getRange(),  MethodHandles.lookup().lookupClass().getSimpleName() );
    }
 

}

class Tree extends Plant{
    int range = 0;
    Tree(){
        this.id = classColor + "TR" + colors.get("reset");
        this.range = 0;
    }
 
    Tree(Zone zone){
        this.zone = zone;
        this.zone.subscribe(this);
        this.id = classColor + "TR" + colors.get("reset");
    }
 
    
    ArrayList<Zone> getReach() {
        return this.getExtention(this.range, new ArrayList<Zone>(Arrays.asList(this.zone)), new ArrayList<Zone>());
    }
   
    static public Supplier<Organism> getSupplier() {

        Supplier<Organism> mkTree = (Zone zone) -> new Tree(zone);
        return mkTree;
    }
    
    static Supply getSupply() {
        return new Supply(getSupplier(), getDigestion(), getRange(),  MethodHandles.lookup().lookupClass().getSimpleName() );
    }
}
 
 
 
class Dispatcher {
    ArrayList<Organism> organisms;
 
    ArrayList<Zone> zones;
    Action actions;
    ZoneManager zoneManager;
    OrganismManager organismManager;
    OrganismFactory organismFactory;
    Dispatcher() {
 
        this.actions = new Action(this);
        this.organisms = new ArrayList<Organism>();
 
        this.zones = new ArrayList<Zone>();
        
    }
    ZoneManager subscribe(ZoneManager zoneManager){
        this.zoneManager = zoneManager;
        return zoneManager;
    }
    OrganismManager subscribe(OrganismManager organismManager){
        this.organismManager = organismManager;
        return organismManager;
    }
    OrganismFactory subscribe(OrganismFactory organismFactory){
        this.organismFactory = organismFactory;
        return organismFactory;
    }

    void subscribe(Organism organism) {

        this.organisms.add(organism);
    }

 
    void subscribe(Zone zone) {
        this.zones.add(zone);
    }
    Organism getNext(){
        return this.organismManager.next();
    }

    void dispatch(Action action){
 
      switch(action.type){
        case CREATE:
            this.organismManager.add(action.supply.supplier.get(action.zone) );
            break;
        case PRINT:
            this.zoneManager.printOnly();
            this.organismManager.printStats();
            break;
        case MOVE:
     
            action.organism.zone.organism = null;
            action.organism.zone = action.zone;
            action.zone.organism = action.organism;

            break;
        case ATTACK:
            Zone newZone = action.enemy.zone;
            newZone.organism = null;
            this.organismManager.deleteOrganism(action.enemy);
            action.organism.zone = newZone;
            
            break;
        default:
            break;
   
      }
                         
    }  
}

   class Action {
    Supplier<Organism> supplier;
    Supply supply;
  
    static boolean isInitialized;
    Organism organism;
    Organism enemy;
    static Dispatcher dispatcher;
    Zone zone;
    Types.ActionType type;
    Action() {

    };
    Action(Dispatcher dispatcherInstance) {
        isInitialized = true;
        dispatcher = dispatcherInstance;

    }

 
    Action(Supply supply, Zone zone){
        this.type = Types.ActionType.CREATE;
        this.supply = supply;
        this.zone = zone;
    }
    Action(Types.ActionType actionType){
        this.type= actionType;
    }

    Action(Organism organism, Zone zone){
        this.type = Types.ActionType.MOVE;
        this.organism = organism;
        this.zone = zone;

    }
    Action(Organism organism, Organism enemy){
        this.type = Types.ActionType.ATTACK;
        this.organism = organism;
        this.enemy = enemy;
    }
 
 
}


class OrganismFactory{

    ArrayList<Supply> supplies;
    Dispatcher dispatcher;
    OrganismFactory(){
 
        this.supplies = new ArrayList<Supply>();
        
    }

    OrganismFactory(Dispatcher dispatcher){
        this.supplies = new ArrayList<Supply>();
        this.dispatcher = dispatcher;
    }

    void supply(Supply supply){
        this.supplies.add(supply);
 
    };
    private Supply get(Digestion digestion){

    int count = this.supplies.stream().filter(el->{return el.digestion== digestion;} ).collect(Collectors.toCollection(ArrayList::new)).size();      
    
        return this.supplies.stream().filter(el->{ return el.digestion== digestion; }).collect(Collectors.toCollection(ArrayList::new) ).get(RandomGenerator.generate(0,   count-1) );

    }
    Supply getPlant(){
        
        return this.get(Digestion.NONE);

    };

    Supply getCarnivore(){
 
        return this.get(Digestion.CARNIVORE);
    }
 
    Supply getHerbivore(){
        return this.get(Digestion.HERBIVORE);
    }


 
 

}
