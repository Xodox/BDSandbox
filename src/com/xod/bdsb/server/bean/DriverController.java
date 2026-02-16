package com.xod.bdsb.server.bean;

import com.xod.bdsb.server.dao.CarDao;
import com.xod.bdsb.server.dao.DriverDao;
import com.xod.bdsb.server.dto.CarDto;
import com.xod.bdsb.server.dto.DriverDto;
import com.xod.bdsb.server.security.BdsbUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/drivers")
public class DriverController {

    @Autowired
    private DriverDao driverDao;
    @Autowired
    private CarDao carDao;

    private static BdsbUserDetails currentUser() {
        Object p = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return p instanceof BdsbUserDetails ? (BdsbUserDetails) p : null;
    }

    /** Current user's driver profile (DRIVER sees self; ADMIN/OFFICER can use if they have driver_id). */
    @RequestMapping(value = "/me", method = RequestMethod.GET)
    public ResponseEntity<DriverDto> getMe() {
        BdsbUserDetails user = currentUser();
        if (user == null) return new ResponseEntity<DriverDto>(HttpStatus.FORBIDDEN);
        if (user.getDriverId() == null) return new ResponseEntity<DriverDto>(HttpStatus.NOT_FOUND);
        DriverDto d = driverDao.findById(user.getDriverId());
        if (d == null) return new ResponseEntity<DriverDto>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<DriverDto>(d, HttpStatus.OK);
    }

    /** Current driver's cars (only for DRIVER or user with driver_id). */
    @RequestMapping(value = "/me/cars", method = RequestMethod.GET)
    public ResponseEntity<List<CarDto>> getMyCars() {
        BdsbUserDetails user = currentUser();
        if (user == null) return new ResponseEntity<List<CarDto>>(HttpStatus.FORBIDDEN);
        if (user.getDriverId() == null) return new ResponseEntity<List<CarDto>>(HttpStatus.NOT_FOUND);
        List<CarDto> cars = carDao.findCarsByDriverId(user.getDriverId());
        return new ResponseEntity<List<CarDto>>(cars, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<DriverDto>> getAll() {
        BdsbUserDetails user = currentUser();
        if (user == null) return new ResponseEntity<List<DriverDto>>(HttpStatus.FORBIDDEN);
        if ("DRIVER".equals(user.getRole())) {
            if (user.getDriverId() == null) return new ResponseEntity<List<DriverDto>>(Collections.<DriverDto>emptyList(), HttpStatus.OK);
            DriverDto d = driverDao.findById(user.getDriverId());
            return new ResponseEntity<List<DriverDto>>(d != null ? Collections.singletonList(d) : Collections.<DriverDto>emptyList(), HttpStatus.OK);
        }
        return new ResponseEntity<List<DriverDto>>(driverDao.findAll(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<DriverDto> getById(@PathVariable Integer id) {
        BdsbUserDetails user = currentUser();
        if (user == null) return new ResponseEntity<DriverDto>(HttpStatus.FORBIDDEN);
        if ("DRIVER".equals(user.getRole()) && !id.equals(user.getDriverId()))
            return new ResponseEntity<DriverDto>(HttpStatus.FORBIDDEN);
        DriverDto d = driverDao.findById(id);
        if (d == null) return new ResponseEntity<DriverDto>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<DriverDto>(d, HttpStatus.OK);
    }

    @RequestMapping(value = "/search/firstName", method = RequestMethod.GET)
    public ResponseEntity<List<DriverDto>> findByFirstName(@RequestParam("value") String firstName) {
        if (isDriver()) return new ResponseEntity<List<DriverDto>>(HttpStatus.FORBIDDEN);
        return new ResponseEntity<List<DriverDto>>(driverDao.findByFirstName(firstName), HttpStatus.OK);
    }

    @RequestMapping(value = "/search/lastName", method = RequestMethod.GET)
    public ResponseEntity<List<DriverDto>> findByLastName(@RequestParam("value") String lastName) {
        if (isDriver()) return new ResponseEntity<List<DriverDto>>(HttpStatus.FORBIDDEN);
        return new ResponseEntity<List<DriverDto>>(driverDao.findByLastName(lastName), HttpStatus.OK);
    }

    @RequestMapping(value = "/search/yearOfBirth", method = RequestMethod.GET)
    public ResponseEntity<List<DriverDto>> findByYearOfBirth(@RequestParam("value") Integer yearOfBirth) {
        if (isDriver()) return new ResponseEntity<List<DriverDto>>(HttpStatus.FORBIDDEN);
        return new ResponseEntity<List<DriverDto>>(driverDao.findByYearOfBirth(yearOfBirth), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<DriverDto> create(@RequestBody DriverDto driver) {
        if (isDriver()) return new ResponseEntity<DriverDto>(HttpStatus.FORBIDDEN);
        Integer id = driverDao.create(driver);
        driver.setId(id);
        return new ResponseEntity<DriverDto>(driver, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<DriverDto> update(@PathVariable Integer id, @RequestBody DriverDto driver) {
        BdsbUserDetails user = currentUser();
        if (user == null) return new ResponseEntity<DriverDto>(HttpStatus.FORBIDDEN);
        if ("DRIVER".equals(user.getRole()) && !id.equals(user.getDriverId()))
            return new ResponseEntity<DriverDto>(HttpStatus.FORBIDDEN);
        if (driverDao.findById(id) == null) return new ResponseEntity<DriverDto>(HttpStatus.NOT_FOUND);
        driver.setId(id);
        DriverDto updated = driverDao.update(driver);
        return new ResponseEntity<DriverDto>(updated, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (isDriver()) return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
        if (driverDao.findById(id) == null) return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        driverDao.delete(id);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    private static boolean isDriver() {
        BdsbUserDetails user = currentUser();
        return user != null && "DRIVER".equals(user.getRole());
    }
}
