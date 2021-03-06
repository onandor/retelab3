package hu.bme.mit.yakindu.analysis.workhere;

import java.util.ArrayList;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.stext.stext.EventDefinition;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

import hu.bme.mit.model2gml.Model2GML;
import hu.bme.mit.yakindu.analysis.modelmanager.ModelManager;

public class Main {
    @Test
    public void test() {
        main(new String[0]);
    }
    
    public static void main(String[] args) {
        ModelManager manager = new ModelManager();
        Model2GML model2gml = new Model2GML();
        
        // Loading model
        EObject root = manager.loadModel("model_input/example.sct");
        
        // Reading model
        Statechart s = (Statechart) root;
        TreeIterator<EObject> iterator = s.eAllContents();
        String prevState = "";
        int stateCount = -1;
        while (iterator.hasNext()) {
            EObject content = iterator.next();
            if(content instanceof State) {
                State state = (State) content;
                stateCount++;
                System.out.println("State neve: " + state.getName());
                if (prevState.equals("")) {
                    prevState = state.getName();
                }
                else {
                    System.out.println("Tranzakcio: " + prevState + "->" + state.getName());
                    prevState = state.getName();
                }
                
                if (state.getOutgoingTransitions().size() == 0) {
                    System.out.println("Csapda: " + state.getName());
                }
                
                if (state.getName().equals("")) {
                    System.out.println("Javasolt nev: State " + stateCount);
                }
            }
        }
        
        generate(root);
        
        // Transforming the model into a graph representation
        String content = model2gml.transform(root);
        // and saving it
        manager.saveFile("model_output/graph.gml", content);
    }
    
    public void print() {
        System.out.println("public static void print(IExampleStatemachine s) {");
        System.out.println("System.out.println(\"W = \" + s.getSCInterface().getWhiteTime())");
        System.out.println("System.out.println(\"B = \" + s.getSCInterface().getBlackTime())");
        System.out.println("System.out.println(\"white event: \" + s.getSCInterface().raiseWhite())");
        System.out.println("System.out.println(\"black event: \" + s.getSCInterface().raiseBlack())");
        System.out.println("System.out.println(\"start event: \" + s.getSCInterface().raiseStart())");
        System.out.println("}");
    }
    
    public static void generate(EObject root) {
        ArrayList<String> variables = new ArrayList<String>();
        ArrayList<String> events = new ArrayList<String>();
        
        Statechart s = (Statechart) root;
        TreeIterator<EObject> iterator = s.eAllContents();
        
        while(iterator.hasNext()) {
        	
            EObject content = iterator.next();
            if (content instanceof EventDefinition) {
                EventDefinition ed = (EventDefinition) content;
                events.add(ed.getName());
            }
            if (content instanceof VariableDefinition) {
                VariableDefinition vd = (VariableDefinition) content;
                variables.add(vd.getName());
            }
        }
        
        System.out.print("public static void main(String[] args) throws IOException {\r\n" +
            "        ExampleStatemachine s = new ExampleStatemachine();\r\n" +
            "        s.setTimer(new TimerService());\r\n" +
            "        RuntimeService.getInstance().registerStatemachine(s, 200);\r\n" +
            "        s.init();\r\n" +
            "        s.enter();\r\n" +
            "        s.runCycle();\r\n" +
            "        print(s);\r\n" +
            "        s.raiseStart();\r\n" +
            "        s.runCycle();\r\n" +
            "        \r\n" +
            "        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));\r\n" +
            "        boolean run = true;\r\n" +
            "        while(run) {\r\n" +
            "            String option = reader.readLine();\r\n" +
            "            \r\n");
        
        for(int i=0;i<events.size();i++){
            System.out.println("\t\t\tif (option.equals( \""+ events.get(i) + "\")) {\r\n" +
                "                print(s);\r\n" +
	            "                s.raise" + events.get(i) + "();\r\n" +
	            "                s.runCycle();\r\n" +
	            "            }\r\n");
                    
        }
        
        System.out.print(
            "          else if (option.equals(\"exit\")) {\r\n" +
            "                run = false;\r\n" +
            "            }\r\n" +
            "        }\r\n" +
            "        \r\n" +
            "        System.exit(0);\r\n" +
            "    }\r\n" +
            "\r\n" +
            "    public static void print(IExampleStatemachine s) {\r\n");
                
        for(int i=0;i<variables.size();i++){
        	System.out.println("\tSystem.out.println(" + variables.get(i) + "= \" + s.getSCInterface().get"+variables.get(i)+"();\r\n");
        }
  
        System.out.print("});");
    }
}
