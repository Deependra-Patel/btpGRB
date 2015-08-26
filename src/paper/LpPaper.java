package paper;

import gurobi.*;
public class LpPaper {

	public static void main(String[] args) {
	    try {
	        GRBEnv    env   = new GRBEnv("mip1.log");
	        GRBModel  model = new GRBModel(env);

	        // Create variables
	        int c = 1;
	        int p = 1;
	        int l = 1;
	        int t = 2; //cloud, single
	        int e = 1;
	        int m = 2;
	        int fixed[][] = {{1, 2}};
	        int var[][] = {{1, 2}};
	        int elas[][] = {{1, 2}};
	        int cap[] = {1};
	        int FP[][][] = {
	        	{{1, 3}, {2, 3}}
	        };
	        int SC[][] = {
	        	{1, 2}
	        };
	        int functionsList[][] = {
	        	{1, 2}
	        };
	        int T[][] = {
	        	{100}
	        };
	        int PC[] = {10};
	        int location[] = {0};
	        int type[] =     {1};
	        float Lat[][][][] = {{{{1,2}}, {{1,2}}}};
	        
	        double M = 1.0;
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
	        
	        GRBVar[][] provFunc = new GRBVar[p][m];
	        for(int i=0; i<p; i++){
	        	for(int j=0; j<m; j++){
		        	String st = "provFunc_" + String.valueOf(i) + "_" + String.valueOf(j);
		        	active[i] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, st); 
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
	        
	      //provFunc(pi, mi)*provFunc(pi2, mi2)
	        GRBVar[][][][] pF2 = new GRBVar[p][m][p][m];
	        for(int i=0; i<p; i++){
	        	for(int j=0; j<m; j++){
	    	        for(int i2=0; i2<p; i2++){
	    	        	for(int j2=0; j2<m; j2++){
	    		        	String st = "pF2_" + String.valueOf(i) + "_" + String.valueOf(j)
	    		        			+"_"+String.valueOf(i2) + "_" + String.valueOf(j2);
	    		        	pF2[i][j][i2][j2] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, st); 
	    	        	}
	    	        }	
	        	}
	        }	        
	        
	        //fb
	        GRBVar[][][][][][] fb = new GRBVar[c][e][p][m][p][m];
	        for(int c1=0; c1<c; c1++){
	        	for(int e1=0; e1<e; e1++){
	        		for(int p1=0; p1<p; p1++){
	        			for(int m1 = 0; m1<m; m1++){
	    	        		for(int p2=0; p2<p; p2++){
	    	        			for(int m2 = 0; m2<m; m2++){
	    	    		        	String st = "f_" + String.valueOf(c1) + "_" + String.valueOf(e1)+"_"+
	    	    		        			String.valueOf(p1)+"_"+String.valueOf(m1)+"_"+String.valueOf(p2)+
	    	    		        			"_"+String.valueOf(m2);
	    	    		        	fb[c1][e1][p1][m1][p2][m2] = model.addVar(0.0, 1.0, 0.0, GRB.CONTINUOUS, st);
	    	        			}
	    	        		}
	        			}
	        		}
	        	}
	        }
	        
	        // Integrate new variables

	        model.update();
	        
	        

	        // Set objective: maximize x + y + 2 z
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

	        
	        for(int ci=0; ci<c; ci++){
	        	for(int ei=0; ei<e; ei++){
	        		for(int pi=0; pi<p; pi++){
	        			for(int mi=0; mi<m; mi++){
	    	        		for(int pi2=0; pi2<p; pi2++){
	    	        			for(int mi2=0; mi2<m; mi2++){
	    	        				GRBLinExpr expr1 = new GRBLinExpr();	    	        			
	    	        				expr1.addTerm(1.0, fb[ci][ei][pi][mi][pi2][mi2]);
	    	        				GRBLinExpr expr2 = new GRBLinExpr();
	    	        				expr2.addTerm(M, pF2[pi][mi][pi2][mi2]);
	    	        				String st = "C2_1";
	    	        		        model.addConstr(expr1, GRB.LESS_EQUAL, expr2, st);
	    	        		        expr1 = new GRBLinExpr();
	    	        		        expr1.addTerm(1.0, fb[ci][ei][pi][mi][pi2][mi2]);
	    	        		        expr2 = new GRBLinExpr();
	    	        		        expr2.addTerm(1.0, f[ci][ei][pi][mi][pi2][mi2]);
	    	        		        model.addConstr(expr1, GRB.LESS_EQUAL, expr2, "C2_2");
	    	        		        expr1 = new GRBLinExpr();
	    	        		        expr1.addTerm(1.0, fb[ci][ei][pi][mi][pi2][mi2]);
	    	        		        expr2 = new GRBLinExpr();
	    	        		        expr2.addTerm(1.0, f[ci][ei][pi][mi][pi2][mi2]);
	    	        		        expr2.addTerm(M, pF2[pi][mi][pi2][mi2]);
	    	        		        expr2.addConstant(-1);
	    	        		        model.addConstr(expr1, GRB.GREATER_EQUAL, expr2, "C2_3");	    	        		      	    	        		       
	    	        			}
	    	        		}	
	        			}
	        		}
	        	}	        	
	        }
	        
			for(int pi=0; pi<p; pi++){
				for(int mi=0; mi<m; mi++){
	        		for(int pi2=0; pi2<p; pi2++){
	        			for(int mi2=0; mi2<m; mi2++){
	        				GRBLinExpr expr1 = new GRBLinExpr();	    	        			
	        				expr1.addTerm(2.0, pF2[pi][mi][pi2][mi2]);
	        				GRBLinExpr expr2 = new GRBLinExpr();
	        				expr2.addTerm(1.0, provFunc[pi][mi]);
	        				expr2.addTerm(1.0, provFunc[pi2][mi2]);
	        				String st = "C3_1";
	        				System.out.println("here");
//	        		        model.addConstr(expr1, GRB.LESS_EQUAL, expr2, "df");
	        		        System.out.println("there");
//	        		        expr1 = new GRBLinExpr();
//	        		        expr1.addTerm(1.0, pF2[pi][mi][pi2][mi2]);
//	        		        expr2 = new GRBLinExpr();
//	        		        expr2.addTerm(1.0, provFunc[pi][mi]);
//	        		        expr2.addTerm(1.0, provFunc[pi2][mi2]);
//	        		        expr2.addConstant(-1);
//	        		        model.addConstr(expr1, GRB.GREATER_EQUAL, expr2, "C2_2");	        				
	        			}
	        		}
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
						for(int mi=0; mi<m; mi++){
			        		for(int pi2=0; pi2<p; pi2++){
			        			for(int mi2=0; mi2<m; mi2++){
			        				expr1.addTerm(1.0, f[ci][ei][pi][functionsList[ci][0]][pi2][mi2]);
			        			}
			        		}
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
						for(int j=0; j<m && j>1; j++){
							for(int mi=0; mi<m && mi==functionsList[ci][j]; mi++){
								GRBLinExpr expr1 = new GRBLinExpr();
								GRBLinExpr expr2 = new GRBLinExpr();
								for(int pi2=0; pi2<p; pi2++){
									for(int mi2=0; mi2<m && mi2==functionsList[ci][j-1]; mi2++){
										expr1.addTerm(1.0, f[ci][ei][pi2][mi2][pi][mi]);
									}
								}

								for(int pi2=0; pi2<p; pi2++){
									for(int mi2=0; mi2<m && mi2==functionsList[ci][j+1]; mi2++){
										expr1.addTerm(1.0, f[ci][ei][pi][mi][pi2][mi2]);
									}
								}
								model.addConstr(expr1, GRB.EQUAL, expr2, "E9");
							}
						}
					}
				}
			}
			
			for(int ci=0; ci<c; ci++){
				for(int ei=0; ei<e; ei++){
					GRBLinExpr expr1 = new GRBLinExpr();
					GRBLinExpr expr2 = new GRBLinExpr();
					for(int pi=0; pi<=p; pi++){
						for(int pi2=0; pi2<=p; pi2++){
							for(int j=1; j<m; j++){
								for(int mi=0; mi<m && mi==functionsList[ci][j]; mi++){
									for(int mi2=0; mi2<m && mi2==functionsList[ci][j+1]; mi2++){
										expr1.addTerm(Lat[pi][mi][pi2][mi2], f[ci][ei][pi][mi][pi2][mi2]);
									}
								}
							}
						}
					}
					expr2.addConstant(PC[ci]);
					model.addConstr(expr1, GRB.LESS_EQUAL, expr2, "E10");
				}
			}
	  	
	        // Optimize model

	        model.optimize();

	        System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));

	        // Dispose of model and environment

	        model.dispose();
	        env.dispose();

	      } catch (GRBException e) {
	        System.out.println("Error code: " + e.getErrorCode() + ". " +
	                           e.getMessage());
	      }
		System.out.println("Deependra");
	}

}
