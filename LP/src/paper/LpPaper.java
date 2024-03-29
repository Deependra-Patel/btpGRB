package paper;

import gurobi.*;
public class LpPaper {

	public static void main(String[] args) {
	    try {
	        GRBEnv    env   = new GRBEnv("mip1.log");
	        GRBModel  model = new GRBModel(env);

	        // Create variables
	        int c = 2; //Number of classes
	        int p = 5; //Number of provisions
	        int l = 3; //Number of locations
	        int t = 2; //Number of types (cloud, single)
	        int e = 2; //Number of epochs
	        int m = 3; //Number of functions 
	        int fixed[][] = {{0, 5}, {0, 5}, {0, 5}}; //Fixed cost [location][type]
	        int var[][] = {{0, 5}, {0, 5}, {0, 5}}; //Variable cost [location][type]
	        int elas[][] = {{3, 0}, {3, 0}, {3, 0}}; //Elastic cost [location][type]
	        int cap[] = {15, 15, 15, 15, 15}; //Cap [provisions]
	        int FP[][][] = { //Per packet processing cost [class][function][type]
	        	{{1, 3}, {2, 3}, {3, 4}},
	        	{{1, 3}, {2, 3}, {3, 4}}
	        };
	        int SC[][] = { //Service chain list of functions for [classes]
	        	{0, 1, 2},
	        	{1, 2}
	        };

	        int T[][] = { //Traffic [class][epoch]
	        	{10, 10}, 
	        	{10, 10}
	        };
	        float PC[] = {15.0f, 15.0f}; //Performance constraint [classes]
	        int location[] = {0, 1, 2, 2, 2}; //Location of provision [provision] -> 0 to l-1
	        int type[] =     {0, 1, 1, 0, 1}; //Type of provision [provision] -> 0 to t-1
	        float Lat[][][][] = {{{{1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}}, {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}}, {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}}}, {{{1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}}, {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}}, {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}}}, {{{1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}}, {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}}, {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}}}, {{{1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}}, {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}}, {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}}}, {{{1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}}, {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}}, {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}}}};
	        //Latency [provision][function][provision][function]
	        
	        //Define decision variables below
	        GRBVar[] active = new GRBVar[p];
	        for(int i=0; i<p; i++){
	        	String st = "active_" + String.valueOf(i);
	        	active[i] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, st); 
	        }
	        
	        GRBVar[] res = new GRBVar[p];
	        for(int i=0; i<p; i++){
	        	String st = "res_" + String.valueOf(i);
	        	res[i] = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.INTEGER, st); 
	        }
	        
	        GRBVar[][] res2 = new GRBVar[p][e];
	        for(int i=0; i<p; i++){
	        	for(int j=0; j<e; j++){
		        	String st = "res2_" + String.valueOf(i) + "_" + String.valueOf(j);
		        	res2[i][j] = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.INTEGER, st); 
	        	}
	        }
	        
	        GRBVar[][][][][][] f = new GRBVar[c][e][p][m][p][m];
	        for(int c1=0; c1<c; c1++){
	        	for(int e1=0; e1<e; e1++){
	        		for(int p1=0; p1<p; p1++){
	        			for(int m1 = 0; m1<m; m1++){
	    	        		for(int p2=0; p2<p; p2++){
	    	        			for(int m2 = 0; m2<m; m2++){
	    	    		        	String st = "f_" + String.valueOf(c1) + "_" + String.valueOf(e1)+"_"+
	    	    		        			String.valueOf(p1)+"_"+String.valueOf(m1)+"_"+String.valueOf(p2)+
	    	    		        			"_"+String.valueOf(m2);
	    	    		        	f[c1][e1][p1][m1][p2][m2] = model.addVar(0.0, 1.0, 0.0, GRB.CONTINUOUS, st);
	    	        			}
	    	        		}
	        			}
	        		}
	        	}
	        }        
	        
	        // Integrate new variables
	        model.update();
	        	        
	        // Set objective
	        GRBLinExpr expr;
	        expr = new GRBLinExpr();
	        for(int pi=0; pi<p; pi++){
	        	expr.addTerm(fixed[location[pi]][type[pi]], active[pi]);
	        	expr.addTerm(var[location[pi]][type[pi]], res[pi]);
	        	for(int ei = 0; ei<e; ei++){
	        		expr.addTerm(elas[location[pi]][type[pi]], res2[pi][ei]);
	        	}
	        }
	        model.setObjective(expr, GRB.MINIMIZE);			
			
			//eq 4

        	for(int ei=0; ei<e; ei++){
        		for(int pi=0; pi<p; pi++){
        			GRBLinExpr expr1 = new GRBLinExpr();
        			GRBLinExpr expr2 = new GRBLinExpr();
        			for(int mi=0; mi<m; mi++){
        				for(int ci=0; ci<c; ci++){
	    	        		for(int pi2=0; pi2<p; pi2++){
	    	        			for(int mi2=0; mi2<m; mi2++){
	    	        				expr1.addTerm(FP[ci][mi][type[pi]]*T[ci][ei], f[ci][ei][pi][mi][pi2][mi2]);	    	        				
	    	        			}
	    	        		}
        				}
        			}
					expr2.addTerm(1.0, res2[pi][ei]);
					model.addConstr(expr1, GRB.EQUAL, expr2, "E4");
        		}
        	}
        	
        	//E5
        	for(int ei=0; ei<e; ei++){
        		for(int pi=0; pi<p; pi++){
        			GRBLinExpr expr1 = new GRBLinExpr();
        			GRBLinExpr expr2 = new GRBLinExpr();
        			for(int mi=0; mi<m; mi++){
        				for(int ci=0; ci<c; ci++){
	    	        		for(int pi2=0; pi2<p; pi2++){
	    	        			for(int mi2=0; mi2<m; mi2++){
	    	        				expr1.addTerm(FP[ci][mi][type[pi]]*T[ci][ei], f[ci][ei][pi][mi][pi2][mi2]);	    	        				
	    	        			}
	    	        		}
        				}
        			}
					expr2.addTerm(1.0, res[pi]);
					model.addConstr(expr1, GRB.LESS_EQUAL, expr2, "E5");
        		}
        	}	        
			for(int pi=0; pi<p; pi++){
				GRBLinExpr expr1 = new GRBLinExpr();	    	        			
				expr1.addTerm(1.0, res[pi]);
				GRBLinExpr expr2 = new GRBLinExpr();
				expr2.addConstant(cap[pi]);
				String st = "E6";
		        model.addConstr(expr1, GRB.LESS_EQUAL, expr2, st);				
			}

			for(int pi=0; pi<p; pi++){
				GRBLinExpr expr1 = new GRBLinExpr();	    	        			
				expr1.addTerm(1.0, res[pi]);
				GRBLinExpr expr2 = new GRBLinExpr();
				expr2.addTerm(cap[pi], active[pi]);
				String st = "E7";
		        model.addConstr(expr1, GRB.LESS_EQUAL, expr2, st);				
			}
			
			for(int ci=0; ci<c; ci++){
				for(int ei=0; ei<e; ei++){
					GRBLinExpr expr1 = new GRBLinExpr();				
					for(int pi=0; pi<p; pi++){
		        		for(int pi2=0; pi2<p; pi2++){		        			
		        			expr1.addTerm(1.0, f[ci][ei][pi][SC[ci][0]][pi2][SC[ci][1]]);
		        		}
					}
					GRBLinExpr expr2 = new GRBLinExpr();
					expr2.addConstant(1.0);
					model.addConstr(expr1, GRB.EQUAL, expr2, "E8");
				}
			}
	  	
			for(int ei=0; ei<e; ei++){
				for(int pi=0; pi<p; pi++){
					for(int ci=0; ci<c; ci++){
						for(int j=1; j<SC[ci].length-1; j++){
							int mi = SC[ci][j];
							GRBLinExpr expr1 = new GRBLinExpr();
							GRBLinExpr expr2 = new GRBLinExpr();
							for(int pi2=0; pi2<p; pi2++){
								int mi2 = SC[ci][j-1];
								expr1.addTerm(1.0, f[ci][ei][pi2][mi2][pi][mi]);
								mi2 = SC[ci][j+1];
								expr2.addTerm(1.0, f[ci][ei][pi][mi][pi2][mi2]);
							}
							model.addConstr(expr1, GRB.EQUAL, expr2, "E9");							
						}
					}
				}
			}
			
			for(int ci=0; ci<c; ci++){
				for(int ei=0; ei<e; ei++){
					GRBLinExpr expr1 = new GRBLinExpr();
					GRBLinExpr expr2 = new GRBLinExpr();
					for(int j=0; j<SC[ci].length-1; j++){
						for(int pi=0; pi<p; pi++){
							for(int pi2=0; pi2<p; pi2++){
								int mi = SC[ci][j];
								int mi2 = SC[ci][j+1];								
								expr1.addTerm(Lat[pi][mi][pi2][mi2], f[ci][ei][pi][mi][pi2][mi2]);
							}
						}
					}
					expr2.addConstant(PC[ci]);
					model.addConstr(expr1, GRB.LESS_EQUAL, expr2, "E10");
				}
			}
			
			
			
//////////////////////////////////////////////////////////////////////////////////////////////////////////	  	
	        // Optimize model
			System.out.println("Solving-----");
	        model.optimize();
	        int status = model.get(GRB.IntAttr.Status);
	        if (status == GRB.Status.UNBOUNDED) {
	          System.out.println("The model cannot be solved "
	              + "because it is unbounded");
	        }
	        else if(status == GRB.Status.INFEASIBLE){
		        model.computeIIS();
	        	System.out.println("Printing constraints that led to infeasibility: #############################");
	        	for(GRBConstr constr: model.getConstrs()){
	        		if(constr.get(GRB.IntAttr.IISConstr)>0){
	        			System.out.println(constr.get(GRB.StringAttr.ConstrName));
	        		}
	        	}
	        	System.out.println("Printing names of variables inconsistent: ");
	        	for(GRBVar v:model.getVars()){
	        	    if (v.get(GRB.IntAttr.IISLB) > 0 || v.get(GRB.IntAttr.IISUB) > 0){
	        	        System.out.println(v.get(GRB.StringAttr.VarName));
	        	    }
	        	}
	        }
	        else if (status == GRB.Status.OPTIMAL) {
	        	System.out.println("Printing optimal solution detail#########################");
	        	System.out.println("The optimal objective is " + model.get(GRB.DoubleAttr.ObjVal));
	        	//int[] activeAns = model.get(GRB.BINARY, active);
			  
			  	System.out.println("Printing f(ci, ei, pi, mi, pi2, mi2) provisions: ");
		        for(int ci=0; ci<c; ci++){
		        	for(int ei=0; ei<e; ei++){
		        		for(int pi=0; pi<p; pi++){
		        			for(int mi=0; mi<m; mi++){
		    	        		for(int pi2=0; pi2<p; pi2++){
		    	        			for(int mi2=0; mi2<m; mi2++){
		    	        				System.out.print(f[ci][ei][pi][mi][pi2][mi2].get(GRB.DoubleAttr.X)+" ");
				        			}
				        		}
							}
						}
					}
				}
		        
	        	System.out.println("\nPrinting active (0,1) provisions: ");
			  	for(int pi=0; pi<p; pi++){
				  	System.out.println(active[pi].get(GRB.DoubleAttr.X));
			  	}				      
	        }
	        else if (status != GRB.Status.INF_OR_UNBD &&
	            status != GRB.Status.INFEASIBLE    ) {
	        	System.out.println("Optimization was stopped with status " + status);	        
	        }

	        System.out.println("Objective (Minimum cost): " + model.get(GRB.DoubleAttr.ObjVal));

	        // Dispose of model and environment

	        model.dispose();
	        env.dispose();

	      } catch (GRBException e) {
	        System.out.println("Error code: " + e.getErrorCode() + ". " +
	                           e.getMessage());
	      }
		System.out.println("- Deependra");
	}

}
