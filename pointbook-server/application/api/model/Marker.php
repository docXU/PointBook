<?php
/**
 * Created by PhpStorm.
 * User: xxw
 * Date: 2017/10/21
 * Time: 21:49
 */

namespace app\api\model;


use think\Model;

class Marker extends Model
{
    public function tags(){
        return $this->hasMany('Tag', 'mid','sid');
    }
}