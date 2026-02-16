package com.xod.bdsb.server.bean;

import com.xod.bdsb.server.dao.CarDao;
import com.xod.bdsb.server.dto.CarDto;
import com.xod.bdsb.server.security.BdsbUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cars")
public class CarController {

    @Autowired
    private CarDao carDao;

    private static BdsbUserDetails currentUser() {
        Object p = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return p instanceof BdsbUserDetails ? (BdsbUserDetails) p : null;
    }

    private static boolean isAdmin() {
        BdsbUserDetails user = currentUser();
        return user != null && "ADMIN".equals(user.getRole());
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<CarDto>> getAll() {
        BdsbUserDetails user = currentUser();
        if (user == null) return new ResponseEntity<List<CarDto>>(HttpStatus.FORBIDDEN);
        List<CarDto> list;
        if ("DRIVER".equals(user.getRole())) {
            if (user.getDriverId() == null) return new ResponseEntity<List<CarDto>>(HttpStatus.FORBIDDEN);
            list = carDao.findCarsByDriverId(user.getDriverId());
        } else {
            list = carDao.findAll();
        }
        return new ResponseEntity<List<CarDto>>(list, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<CarDto> getById(@PathVariable Integer id) {
        BdsbUserDetails user = currentUser();
        if (user == null) return new ResponseEntity<CarDto>(HttpStatus.FORBIDDEN);
        CarDto car = carDao.findById(id);
        if (car == null) return new ResponseEntity<CarDto>(HttpStatus.NOT_FOUND);
        if ("DRIVER".equals(user.getRole())) {
            if (user.getDriverId() == null) return new ResponseEntity<CarDto>(HttpStatus.FORBIDDEN);
            List<CarDto> myCars = carDao.findCarsByDriverId(user.getDriverId());
            boolean allowed = myCars.stream().anyMatch(c -> c.getId().equals(id));
            if (!allowed) return new ResponseEntity<CarDto>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<CarDto>(car, HttpStatus.OK);
    }

    /** Create car (ADMIN only). */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<CarDto> create(@RequestBody CarDto car) {
        if (!isAdmin()) return new ResponseEntity<CarDto>(HttpStatus.FORBIDDEN);
        Integer id = carDao.create(car);
        car.setId(id);
        return new ResponseEntity<CarDto>(car, HttpStatus.CREATED);
    }

    /** Update car (ADMIN only). */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<CarDto> update(@PathVariable Integer id, @RequestBody CarDto car) {
        if (!isAdmin()) return new ResponseEntity<CarDto>(HttpStatus.FORBIDDEN);
        if (carDao.findById(id) == null) return new ResponseEntity<CarDto>(HttpStatus.NOT_FOUND);
        car.setId(id);
        CarDto updated = carDao.update(car);
        return new ResponseEntity<CarDto>(updated, HttpStatus.OK);
    }

    /** Delete car (ADMIN only). */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!isAdmin()) return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
        if (carDao.findById(id) == null) return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        carDao.delete(id);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    /** Get driver IDs assigned to this car (ADMIN only, for edit form). */
    @RequestMapping(value = "/{id}/drivers", method = RequestMethod.GET)
    public ResponseEntity<List<Integer>> getDriverIds(@PathVariable Integer id) {
        if (!isAdmin()) return new ResponseEntity<List<Integer>>(HttpStatus.FORBIDDEN);
        if (carDao.findById(id) == null) return new ResponseEntity<List<Integer>>(HttpStatus.NOT_FOUND);
        List<Integer> driverIds = carDao.findDriverIdsByCarId(id);
        return new ResponseEntity<List<Integer>>(driverIds, HttpStatus.OK);
    }

    /** Set drivers for this car (ADMIN only). Replaces existing links. */
    @RequestMapping(value = "/{id}/drivers", method = RequestMethod.PUT)
    public ResponseEntity<Void> setDrivers(@PathVariable Integer id, @RequestBody List<Integer> driverIds) {
        if (!isAdmin()) return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
        if (carDao.findById(id) == null) return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        carDao.setDriversForCar(id, driverIds != null ? driverIds : java.util.Collections.emptyList());
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
}
