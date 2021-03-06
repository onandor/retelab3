package hu.bme.mit.yakindu.analysis.workhere;

import java.io.IOException;
import hu.bme.mit.yakindu.analysis.RuntimeService;
import hu.bme.mit.yakindu.analysis.TimerService;
import hu.bme.mit.yakindu.analysis.example.ExampleStatemachine;
import hu.bme.mit.yakindu.analysis.example.IExampleStatemachine;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class RunStatechart {
	
	public static void main(String[] args) throws IOException {
		ExampleStatemachine s = new ExampleStatemachine();
		s.setTimer(new TimerService());
		RuntimeService.getInstance().registerStatemachine(s, 200);
		s.init();
		s.enter();
		s.runCycle();
		print(s);
		s.raiseStart();
		s.runCycle();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		boolean run = true;
		while(run) {
			String option = reader.readLine();
			
			if (option.equals("white")) {
				print(s);
				s.raiseWhite();
				s.runCycle();
			}
			else if (option.equals("black")) {
				print(s);
				s.raiseBlack();
				s.runCycle();
			}
			else if (option.equals("exit")) {
				run = false;
			}
		}
		
		System.exit(0);
	}

	public static void print(IExampleStatemachine s) {
		System.out.println("W = " + s.getSCInterface().getWhiteTime());
		System.out.println("B = " + s.getSCInterface().getBlackTime());
	}
}
