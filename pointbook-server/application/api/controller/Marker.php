<?php
/**
 * Created by PhpStorm.
 * User: xxw
 * Date: 2017/10/21
 * Time: 21:19
 */

namespace app\api\controller;


use think\Controller;
use think\Db;
use think\Exception;
use think\Request;
use \app\api\model\Marker as MarkerModel;

class Marker extends Controller
{
    public function get()
    {
        $requestIns = Request::instance();

        $lat = $requestIns->param('lat');
        $lon = $requestIns->param('lon');
        $scope = $requestIns->param('scope');

        //0.01的精度约等于1000米，以此作为计算因子
        $latScope = 0.01 * intval($scope);
        $lonScope = 0.01 * intval($scope);
        $markerModel = new MarkerModel();
        //bug: 临界范围不能用此法比较
        $res = $markerModel->where('lat', '>', (floatval($lat) - $latScope))
            ->where('lat', '<', (floatval($lat) + $latScope))
            ->where('lon', '>', (floatval($lon) - $lonScope))
            ->where('lon', '<', (floatval($lon) + $lonScope))
            ->select();

        $result = [];
        foreach ($res as $marker) {
            $tags = Db::table('tbl_marker_tag')
                ->where('mid', $marker->sid)
                ->select();
            $marker['tags'] = $tags;
            $result[] = $marker;
        }
        return json($result, 200);

    }

    public function add()
    {
        $requestIns = Request::instance();
        $lat = $requestIns->param('lat');
        $lon = $requestIns->param('lon');
        $title = $requestIns->param('title');

        try {
            $marker = new MarkerModel([
                'sid' => 'r' . time() . rand(20, 10000),
                'lat' => $lat,
                'lon' => $lon,
                'title' => $title
            ]);
            $marker->save();
        } catch (Exception $e) {
            echo $e->getMessage();
        }
        return json($marker->sid, 200);
    }
}