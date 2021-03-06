package br.ufrgs.inf.gar.condo.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.hibernate.Session;

import br.ufrgs.inf.gar.condo.domain.Apartment;
import br.ufrgs.inf.gar.condo.domain.Condominium;
import br.ufrgs.inf.gar.condo.domain.Employee;
import br.ufrgs.inf.gar.condo.domain.Garage;
import br.ufrgs.inf.gar.condo.domain.Lamp;
import br.ufrgs.inf.gar.condo.domain.Sector;
import br.ufrgs.inf.gar.condo.simulator.persistence.SessionFactoryService;

/**
 * Condominium Simulator
 * 
 * Cria um condominio na base de dados e se mantem ativo atualizando
 * o consumo de luz e agua, alem do numero de pessoas presentes nos locais.
 *
 */
public class Simulator {
	
	private static final long INTERVAL_ONE = 8000;
	private static final long INTERVAL_TWO = 12000;
	private static Integer condominiumId;
	private static Integer sectorGarageId;
	private static Integer sectorFirstFloorId;
	private static Integer sectorSecondFloorId;
	private static final List<Integer> APTS = new ArrayList<>();
	private static final List<Integer> EMPS = new ArrayList<>();
	private static final List<Integer> GARS = new ArrayList<>();
	private static final List<Integer> LAMPS = new ArrayList<>();
	
    public static void main( String[] args ) {
        System.out.println( "test started" );
        
        createData();
        startUpdater();
    }

	private static void startUpdater() {
        final Thread threadOne = new Thread() {
			public void run() {
				final Random random = new Random();
				Session session;
				try {
					while (true) {
						session = SessionFactoryService.get().openSession();
						session.beginTransaction();
			            Condominium condo = (Condominium) session.get(Condominium.class, condominiumId);
			            //acumulado de 5 segundos, devido ao interalo da thread
			            condo.getInstantElectricUsage().set(generateInstantElectricUsage(random));  //ex: 0.449
			            condo.getTotalElectricUsage().add(condo.getInstantElectricUsage().toFloat());
			            condo.getInstantWaterUsage().set(generateInstantWaterUsage(random));  //ex: 0.008
			            condo.getTotalWaterUsage().add(condo.getInstantWaterUsage().toFloat());
			            if (random.nextInt(10) == 1) 
			            	condo.setNumUnknownPeople(random.nextInt(10));
			            else condo.setNumUnknownPeople(0);

			            session.save(condo);
			            session.getTransaction().commit();
			            
			            session.beginTransaction();	
			            for (Integer aptId : APTS) {
				            Apartment apt = (Apartment) session.get(Apartment.class, aptId);
				            apt.getInstantElectricUsage().set(generateInstantElectricUsage(random));  
				            apt.getTotalElectricUsage().add(apt.getInstantElectricUsage().toFloat());
				            apt.getInstantWaterUsage().set(generateInstantWaterUsage(random));  
				            apt.getTotalWaterUsage().add(apt.getInstantWaterUsage().toFloat());
				            session.save(apt);
			            }
			            session.getTransaction().commit();
			            session.close();
						Thread.sleep(INTERVAL_ONE);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e) {
					System.out.println("thread stopped");
					e.printStackTrace();
				}
			}
		};
		threadOne.start();
		
		final Thread thread = new Thread() {
			public void run() {
				final Random random = new Random();
				Session session;
				try {
					while (true) {
						session = SessionFactoryService.get().openSession();
						session.beginTransaction();
			            for (Integer aptId : APTS) {
				            Apartment apt = (Apartment) session.get(Apartment.class, aptId);
				            apt.setNumPeople(generateNumPeople(random));
				            session.save(apt);
			            }
			            session.getTransaction().commit();
			            
			            session.beginTransaction();
			            for (Integer employeeId : EMPS) {
				            Employee employee = (Employee) session.get(Employee.class, employeeId);
				            employee.setWorking(random.nextBoolean());
				            session.save(employee);
			            }
			            session.getTransaction().commit();
			            
			            session.beginTransaction();
			            for (Integer garageId : GARS) {
				            Garage garage = (Garage) session.get(Garage.class, garageId);
				            garage.setOccupied(random.nextBoolean());
				            session.save(garage);
			            }
			            session.getTransaction().commit();
			            session.close();
						Thread.sleep(INTERVAL_TWO);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e) {
					System.out.println("thread stopped");
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}
	
	private static int generateNumPeople(Random random) {
		return random.nextInt(4);
	}

	private static Float generateInstantElectricUsage(Random random) {
		return random.nextFloat() * 20f;
	};
	
	private static Float generateInstantWaterUsage(Random random) {
		return random.nextFloat() * 13f;
	};

	private static void createData() {
		final Session session = SessionFactoryService.get().openSession();
        session.beginTransaction();
		condominiumId = (Integer) session.save(new Condominium("Floresta", "Bento Gonçalves 133", "Silveira"));
        Condominium condo = (Condominium) session.get(Condominium.class, condominiumId);
        
        sectorGarageId = (Integer) session.save(new Sector("Garagem", condo));
        Sector sectorGarage = (Sector) session.get(Sector.class, sectorGarageId);
        sectorFirstFloorId = (Integer) session.save(new Sector("Primeiro andar", condo));
        Sector sectorFirstFloor = (Sector) session.get(Sector.class, sectorFirstFloorId);
        sectorSecondFloorId = (Integer) session.save(new Sector("Segundo andar", condo));
        Sector sectorSecondFloor = (Sector) session.get(Sector.class, sectorSecondFloorId);
        
        APTS.add((Integer) session.save(new Apartment(101, "Soares", 2, sectorFirstFloor)));
        APTS.add((Integer) session.save(new Apartment(102, "Silva", 2, sectorFirstFloor)));
        APTS.add((Integer) session.save(new Apartment(103, "Roberto", 3, sectorFirstFloor)));
        APTS.add((Integer) session.save(new Apartment(104, "Bento", 3, sectorFirstFloor)));
        APTS.add((Integer) session.save(new Apartment(201, "Juarez", 2, sectorSecondFloor)));
        APTS.add((Integer) session.save(new Apartment(202, "Jonas", 2, sectorSecondFloor)));
        APTS.add((Integer) session.save(new Apartment(203, "Jonatas", 2, sectorSecondFloor)));
        APTS.add((Integer) session.save(new Apartment(204, "Noeli", 2, sectorSecondFloor)));
        
        EMPS.add((Integer) session.save(new Employee("Juarez", "Porteiro", 1000, 40, condo)));
        EMPS.add((Integer) session.save(new Employee("Roberto", "Faxineiro", 1000, 40, condo)));
        EMPS.add((Integer) session.save(new Employee("Ronaldo", "Porteiro", 1000, 40, condo)));
        
        GARS.add((Integer) session.save(new Garage(1, (Apartment) session.get(Apartment.class, APTS.get(0)), sectorGarage)));
        GARS.add((Integer) session.save(new Garage(2, (Apartment) session.get(Apartment.class, APTS.get(1)), sectorGarage)));
        GARS.add((Integer) session.save(new Garage(3, null, sectorGarage)));
        GARS.add((Integer) session.save(new Garage(4, null, sectorGarage)));
        
        LAMPS.add((Integer) session.save(new Lamp(sectorGarage)));
        LAMPS.add((Integer) session.save(new Lamp(sectorGarage)));
        LAMPS.add((Integer) session.save(new Lamp(sectorFirstFloor)));
        LAMPS.add((Integer) session.save(new Lamp(sectorSecondFloor)));
        
        session.getTransaction().commit();
        session.close();
	}
}
